package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

public class HandleBehaviorExprHeadNode extends Node {

    @Child
    AbstractFunctionality functionality;

    private final Object args = new Object[0];

    public HandleBehaviorExprHeadNode(RubyContext context, SourceSection sourceSection) {
        functionality = new UninitializedFunctionality(context);
    }

    public static HandleBehaviorExprHeadNode createHandleBehaviorExprNode(RubyContext context, SourceSection section) {
        return new HandleBehaviorExprHeadNode(context, section);
    }


    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        functionality.execute(frame, self, lastNode);
    }

}

abstract class AbstractFunctionality extends Node {

    protected RubyContext context;


    AbstractFunctionality(RubyContext context) {
        this.context = context;
    }

    abstract public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode);

    protected HandleBehaviorExprHeadNode getHeadNode() {
        return NodeUtil.findParent(this, HandleBehaviorExprHeadNode.class);
    }
}

class AllFunctionality extends AbstractFunctionality {
    @Child
    NormalBehavior normal;
    @Child
    FoldNode fold;
    @Child
            FilterNode filter;

    AllFunctionality(RubyContext context) {
        super(context);
        normal = new NormalBehavior(context, null);
        fold = new FoldNode(context);
        filter = new FilterNode(context);
    }

    @Override
    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        if (self.isNormal()) {
            normal.execute(frame, self, lastNode);
        } else if (self.isFold()) {
            fold.execute(frame, self, lastNode);
        } else if (self.isFilter()) {
            filter.execute(frame,self,lastNode);
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
    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        if (self.getType() == type) {
            functionality.execute(frame, self, lastNode);
        } else {
            next.execute(frame, self, lastNode);
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
    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
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
            } else {
                newFunctionality = null;
            }

            depth += 1;
        }
        propNode.replace(newFunctionality);
        newFunctionality.execute(frame, self, lastNode);
    }


}