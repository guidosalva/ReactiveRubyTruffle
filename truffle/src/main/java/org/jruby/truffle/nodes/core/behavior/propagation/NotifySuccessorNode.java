package org.jruby.truffle.nodes.core.behavior.propagation;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.BehaviorObject;

class NotifySuccessorNode extends Node {
    @Child
    StartPropagation prop;

    NotifySuccessorNode(RubyContext context, SourceSection section) {
        super(section);
        prop = new StartPropagationUninitialized(context, section);
    }

    public void execute(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed) {
        prop.execute(frame, self, sourceId, changed);
    }
}


abstract class StartPropagation extends Node {


    abstract void execute(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed);

    protected NotifySuccessorNode getHeadNode() {
        return NodeUtil.findParent(this, NotifySuccessorNode.class);
    }

}

class StartPropagationConst extends StartPropagation {
    private final int numSignalsDependOnSelf;
    @Child
    protected
    CallDispatchHeadNode callDependentSignals;
    @Child
    protected
    ReadInstanceVariableNode readValue;
    @Child
    protected StartPropagation next;

    public StartPropagationConst(RubyContext context, SourceSection section, int numSignalsDependOnSelf, StartPropagation next) {
        callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        readValue = new ReadInstanceVariableNode(context, section, BehaviorOption.VALUE_VAR, new SelfNode(context, section), false);
        this.numSignalsDependOnSelf = numSignalsDependOnSelf;
        this.next = next;
    }

    @Override
    void execute(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed) {
        if (self.getSignalsThatDependOnSelf().length == numSignalsDependOnSelf) {
            callDepSigs(frame, self, sourceId, changed);
        } else {
            next.execute(frame, self, sourceId, changed);
        }
    }

    @ExplodeLoop
    void callDepSigs(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed) {
        final Object[] args = BehaviorOption.createBehaviorPropagationArgs(sourceId, self, changed);
        final BehaviorObject[] sigs = self.getSignalsThatDependOnSelf();
        for (int i = 0; i < numSignalsDependOnSelf; i++) {
            callDependentSignals.call(frame, sigs[i], "propagation", null, args);
        }
    }
}

class StartPropagationVariable extends StartPropagation {
    @Child
    protected CallDispatchHeadNode callDependentSignals;
    @Child
    protected ReadInstanceVariableNode readValue;

    StartPropagationVariable(RubyContext context, SourceSection section) {
        callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        readValue = new ReadInstanceVariableNode(context, section, BehaviorOption.VALUE_VAR, new SelfNode(context, section), false);
    }

    @Override
    void execute(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed) {
        final BehaviorObject[] signals = self.getSignalsThatDependOnSelf();
        for (BehaviorObject s : signals) {
            callDependentSignals.call(frame, s, "propagation", null, BehaviorOption.createBehaviorPropagationArgs(sourceId, self, changed));
        }
    }
}


class StartPropagationUninitialized extends StartPropagation {
    static final int MAX_CHAIN_SIZE = 3;
    private final RubyContext context;
    private int depth = 0;

    StartPropagationUninitialized(RubyContext context, SourceSection section) {
        this.context = context;
    }

    @Override
    void execute(VirtualFrame frame, BehaviorObject self, long sourceId, boolean changed) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        StartPropagation propNode = getHeadNode().prop;
        StartPropagation newPropNode;
        if (depth >= MAX_CHAIN_SIZE) {
            newPropNode = new StartPropagationVariable(context, getSourceSection());
        } else {
            newPropNode = new StartPropagationConst(context, getSourceSection(), self.getSignalsThatDependOnSelf().length, propNode);
            depth += 1;
        }
        propNode.replace(newPropNode);
        newPropNode.execute(frame, self, sourceId, changed);
    }

}