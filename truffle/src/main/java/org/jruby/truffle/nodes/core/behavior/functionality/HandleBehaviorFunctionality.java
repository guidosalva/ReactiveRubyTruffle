package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

import static org.jruby.truffle.runtime.signalRuntime.BehaviorObject.*;

public class HandleBehaviorFunctionality extends Node {

    @Child
    AbstractFunctionality functionality;

    private final Object args = new Object[0];

    public HandleBehaviorFunctionality(RubyContext context, SourceSection sourceSection) {
        functionality = new UninitializedFunctionality(context);
    }

    public static HandleBehaviorFunctionality createHandleBehaviorExprNode(RubyContext context, SourceSection section) {
        return new HandleBehaviorFunctionality(context, section);
    }


    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        return functionality.execute(frame, self, lastNode);
    }

}

abstract class AbstractFunctionality extends Node {

    protected RubyContext context;


    AbstractFunctionality(RubyContext context) {
        this.context = context;
    }

    /**
     * This method handles the behavior "expression". For special behaviors like e.g. fold the behavior expression is
     * replaced by a predefined expression
     * <p/>
     * This method returns true if the value of the behavior changed
     *
     * @param frame
     * @param self
     * @param lastNode
     * @return
     */
    abstract public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode);

    protected HandleBehaviorFunctionality getHeadNode() {
        return NodeUtil.findParent(this, HandleBehaviorFunctionality.class);
    }
}

class AllFunctionality extends AbstractFunctionality {
    @Child
    NormalBehavior normal;
    @Child
    FoldNode fold;
    @Child
    FilterNode filter;
    @Child
    MapNode map;
    @Child
    MergeNode merge;

    AllFunctionality(RubyContext context) {
        super(context);
        normal = new NormalBehavior(context, null);
        fold = new FoldNode(context);
        filter = new FilterNode(context);
        map = new MapNode(context);
        merge = new MergeNode(context);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        if (self.isNormal()) {
            return normal.execute(frame, self, lastNode);
        } else if (self.isFold()) {
            return fold.execute(frame, self, lastNode);
        } else if (self.isFilter()) {
            return filter.execute(frame, self, lastNode);
        } else if (self.getType() == TYPE_MAP) {
            return map.execute(frame, self, lastNode);
        } else if (self.getType() == TYPE_MERGE) {
            return merge.execute(frame, self, lastNode);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new RuntimeException("the type of the BehaviorObject is unknown: " + self.getType());
    }
}

class CachedFunctionality extends AbstractFunctionality {
    private final int type;
    @Child
    protected AbstractFunctionality next;
    @Child
    private Functionality functionality;


    CachedFunctionality(RubyContext context, Functionality functionality, int type, AbstractFunctionality next) {
        super(context);
        this.next = next;
        this.functionality = functionality;
        this.type = type;
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        if (self.getType() == type) {
            return functionality.execute(frame, self, lastNode);
        } else {
            return next.execute(frame, self, lastNode);
        }
    }
}

class UninitializedFunctionality extends AbstractFunctionality {
    static final int MAX_CHAIN_SIZE = 3;
    private int depth = 0;

    UninitializedFunctionality(RubyContext context) {
        super(context);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        AbstractFunctionality propNode = getHeadNode().functionality;
        AbstractFunctionality newFunctionality;
        if (depth >= MAX_CHAIN_SIZE) {
            newFunctionality = new AllFunctionality(context);
        } else {
            if (self.isNormal()) {
                newFunctionality = new CachedFunctionality(context, new NormalBehavior(context, null), BehaviorObject.TYPE_NORMAL, propNode);
            } else if (self.isFold()) {
                newFunctionality = new CachedFunctionality(context, new FoldNode(context), BehaviorObject.TYPE_FOLD, propNode);
            } else if (self.isFilter()) {
                newFunctionality = new CachedFunctionality(context, new FilterNode(context), BehaviorObject.TYPE_FILTER, propNode);
            } else if (self.isMerge()) {
                newFunctionality = new CachedFunctionality(context, new MergeNode(context), BehaviorObject.TYPE_MERGE, propNode);
            } else if (self.getType() == TYPE_MAP) {
                newFunctionality = new CachedFunctionality(context, new MapNode(context), TYPE_MAP, propNode);
            } else {
                newFunctionality = null;
            }

            depth += 1;
        }
        propNode.replace(newFunctionality);
        return newFunctionality.execute(frame, self, lastNode);
    }


}