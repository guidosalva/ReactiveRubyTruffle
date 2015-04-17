package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
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

    PropagationNode next;
    @Child CallSignalExecAndContinuePropagation execAndPropagate;

    public PropagationSimpleCachedNode(RubyContext context, SourceSection section, PropagationNode next) {
        super(context, section);
        this.next = next;
        execAndPropagate = new CallSignalExecAndContinuePropagation(context,section);
    }

    @Override
    public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
        if (self.isChain()) {
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
        if (self.getSourceToSelfPathCount().length > idxOfSource
                && sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
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
            if(self.isChain()){
                newPropNode = new PropagationSimpleCachedNode(context,getSourceSection(),propNode);
            }else{
                newPropNode = new PropagationCachedNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId, numSources);
            }
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
    StartPropagationHead prop;

    CallSignalExecAndContinuePropagation(RubyContext context, SourceSection section){
        super(section);
        prop = new StartPropagationHead(context,section);
        this.execSigExpr = new ExecSignalExprNode(context, section);
    }

    public void execute(VirtualFrame frame, SignalRuntime self,long sourceId){
        execSigExpr.execSigExpr(frame, self);
        self.setCount(0);
        prop.execute(frame,self,sourceId);
    }
}

class StartPropagationHead extends Node {
    @Child StartPropagation propagation;


    public StartPropagationHead(RubyContext context, SourceSection section) {
        propagation = new StartPropagationUninitialized(context);
    }

    public void execute(VirtualFrame frame, SignalRuntime self, long sourceId){
        propagation.execute(frame,self,sourceId);
    }
}

abstract class StartPropagation extends Node {
    abstract void execute(VirtualFrame frame, SignalRuntime self, long sourceId);
    protected StartPropagationHead getHeadNode() {
        return NodeUtil.findParent(this, StartPropagationHead.class);
    }

}
class StartPropagationConst extends StartPropagation{
    private final int numSignalsDependOnSelf;
    @Child
    CallDispatchHeadNode callDependentSignals;
    @Child StartPropagation next;

    public StartPropagationConst(RubyContext context, int numSignalsDependOnSelf, StartPropagation next) {
        callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        this.numSignalsDependOnSelf = numSignalsDependOnSelf;
        this.next = next;
    }

    @Override
    void execute(VirtualFrame frame, SignalRuntime self, long sourceId) {
        if(self.getSignalsThatDependOnSelf().length == numSignalsDependOnSelf){
            callDepSigs(frame,self,sourceId);
        }else{
            next.execute(frame,self,sourceId);
        }
    }

    @ExplodeLoop
    void callDepSigs(VirtualFrame frame, SignalRuntime self, long sourceId){
        final SignalRuntime[] sigs = self.getSignalsThatDependOnSelf();
        for(int i = 0; i < numSignalsDependOnSelf; i++){
            callDependentSignals.call(frame, sigs[i], "propagation", null, sourceId);
        }
    }
}

class StartPropagationVariable extends StartPropagation{
    @Child
    CallDispatchHeadNode callDependentSignals;

    StartPropagationVariable(RubyContext context){
        callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
    }

    @Override
    void execute(VirtualFrame frame, SignalRuntime self, long sourceId) {
        final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
        for (SignalRuntime s : signals) {
            callDependentSignals.call(frame, s, "propagation", null, sourceId);
        }
    }
}


class StartPropagationUninitialized extends StartPropagation {
    static final int MAX_CHAIN_SIZE = 3;
    private final RubyContext context;
    private int depth = 0;

    StartPropagationUninitialized(RubyContext context){
        this.context = context;
    }

    @Override
    void execute(VirtualFrame frame, SignalRuntime self, long sourceId) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        StartPropagation propNode = getHeadNode().propagation;
        StartPropagation newPropNode;
        if (depth >= MAX_CHAIN_SIZE) {
            newPropNode = new StartPropagationVariable(context);
        } else {
            newPropNode = new StartPropagationConst(context,self.getSignalsThatDependOnSelf().length,propNode);
            depth += 1;
        }
        propNode.replace(newPropNode);
        newPropNode.execute(frame, self, sourceId);
    }

}

