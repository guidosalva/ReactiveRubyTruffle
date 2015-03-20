package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.Ruby;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

import static org.jruby.truffle.nodes.core.BehaviorSimpleNode.*;


public class BehaviorPropagationHeadNode extends Node {
    @Child
    PropagationNode propagationNode;

    public BehaviorPropagationHeadNode(RubyContext context, SourceSection section) {
        super(section);
        propagationNode = new PropagationUninitializedNode(context, section);
    }

    public void handlePropagation(VirtualFrame frame, SignalRuntime self, long sourceId) {
        propagationNode.propagate(frame, self, sourceId);
    }


}

abstract class PropagationNode extends Node {
    protected final RubyContext context;

    public PropagationNode(RubyContext context, SourceSection section) {
        super(section);
        this.context = context;
    }


    public abstract void propagate(VirtualFrame frame, SignalRuntime self, long sourceId);

    protected BehaviorPropagationHeadNode getHeadNode() {
        return NodeUtil.findParent(this, BehaviorPropagationHeadNode.class);
    }


}

class PropagationSimpleCachedNode extends PropagationNode {

    private final int idxOfSource;
    private final long sourceId;

    @Child
    PropagationNode next;
    @Child CallSignalExecAndContinuePropagation execAndPropagate;

    public PropagationSimpleCachedNode(RubyContext context, SourceSection section, PropagationNode next, int idxOfSource, long sourceId) {
        super(context, section);
        this.idxOfSource = idxOfSource;
        this.sourceId = sourceId;
        this.next = next;
        execAndPropagate = new CallSignalExecAndContinuePropagation(context,section);
    }

    @Override
    public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
        if (sourceId == self.getSourceToSelfPathCount()[idxOfSource][0] && self.getSourceToSelfPathCount()[idxOfSource][0] == 1) {
            execAndPropagate.execute(frame,self,sourceId);
        } else {
            next.propagate(frame, self, sourceId);
        }
    }
}

class PropagationCachedNode extends PropagationNode {


    private final int idxOfSource;
    private final long sourceId;
    private final long numSources;
    @Child CallSignalExecAndContinuePropagation execAndPropagate;
    @Child
    PropagationNode next;

    public PropagationCachedNode(RubyContext context, SourceSection section, PropagationNode propNode, int idxOfSource, long sourceId, long numSources) {
        super(context, section);
        this.next = propNode;
        this.idxOfSource = idxOfSource;
        this.sourceId = sourceId;
        this.numSources = numSources;
        execAndPropagate = new CallSignalExecAndContinuePropagation(context,section);
    }


    @Override
    public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
        if (sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
            final int count = self.getCount() + 1;
            if (count >= self.getSourceToSelfPathCount()[idxOfSource][1]) {
                execAndPropagate.execute(frame,self,sourceId);
            } else {
                self.setCount(count);
            }
        } else {
            next.propagate(frame, self, sourceId);
        }
    }
}

class PropagationPolymorphNode extends PropagationNode {
    @Child CallSignalExecAndContinuePropagation execAndPropagate;


    public PropagationPolymorphNode(RubyContext context, SourceSection section) {
        super(context, section);
        execAndPropagate = new CallSignalExecAndContinuePropagation(context,section);
    }

    @Override
    public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
        final long[][] souceToPath = self.getSourceToSelfPathCount();
        for(int i = 0; i < souceToPath.length; i++){
            if(souceToPath[i][0] == sourceId){
                final int count = self.getCount()+1;
                if( count  == souceToPath[i][1]){
                    execAndPropagate.execute(frame,self,sourceId);
                }else{
                    self.setCount(count);
                }
            }
        }
    }
}

class PropagationUninitializedNode extends PropagationNode {
    static final int MAX_CHAIN_SIZE = 3;
    private int depth = 0;

    public PropagationUninitializedNode(RubyContext context, SourceSection section) {
        super(context, section);
    }


    @Override
    public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        final long numSources = numPaths(self, sourceId);
        BehaviorPropagationHeadNode headNode = getHeadNode();
        PropagationNode propNode = getHeadNode().propagationNode;


        PropagationNode newPropNode;
        if (depth >= MAX_CHAIN_SIZE) {
            newPropNode = new PropagationPolymorphNode(context, getSourceSection());
        } else {
                newPropNode = new PropagationCachedNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId, numSources);
            depth += 1;
        }
        propNode.replace(newPropNode);
        newPropNode.propagate(frame,self,sourceId);
    }

    private long numPaths(SignalRuntime self, long sourceId) {
        for (int i = 0; i < self.getSourceToSelfPathCount().length; i++) {
            if (sourceId == self.getSourceToSelfPathCount()[i][0]) {
                return self.getSourceToSelfPathCount()[i][1];
            }
        }
        return -1;
    }
}

class CallSignalExecAndContinuePropagation extends  Node{
    @Child
    ExecSignalExprNode execSigExpr;
    @Child
    CallDispatchHeadNode callDependentSignals;

    CallSignalExecAndContinuePropagation(RubyContext context, SourceSection section){
        super(section);
        callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        this.execSigExpr = new ExecSignalExprNode(context, section);
    }

    public void execute(VirtualFrame frame, SignalRuntime self,long sourceId){
        execSigExpr.execSigExpr(frame, self);
        self.setCount(0);
        final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
        for (SignalRuntime s : signals) {
            callDependentSignals.call(frame, s, "propagation", null, sourceId);
        }
    }


}