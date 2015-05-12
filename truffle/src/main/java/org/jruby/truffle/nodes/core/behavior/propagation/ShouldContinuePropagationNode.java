package org.jruby.truffle.nodes.core.behavior.propagation;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;


public abstract class ShouldContinuePropagationNode extends Node {
    protected final RubyContext context;

    public ShouldContinuePropagationNode(RubyContext context, SourceSection section) {
        super(section);
        this.context = context;
    }

    public static ShouldContinuePropagationNode createUninitializedShouldPropagationNode(RubyContext context, SourceSection section) {
        return new PropagationUninitializedNode(context, section);
    }

    public abstract boolean shouldContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode);

    protected BehaviorPropagationHeadNode getHeadNode() {
        return NodeUtil.findParent(this, BehaviorPropagationHeadNode.class);
    }


}

class PropagationSimpleCachedNode extends ShouldContinuePropagationNode {
    @Child
    ShouldContinuePropagationNode next;
    @Child
    HandlePropagation execAndPropagate;

    public PropagationSimpleCachedNode(RubyContext context, SourceSection section, ShouldContinuePropagationNode next) {
        super(context, section);
        this.next = next;
        execAndPropagate = new HandlePropagation(context, section);
    }

    @Override
    public boolean shouldContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode) {
        if (self.isChain()) {
            return true;
            //execAndPropagate.execute(frame,self,args);
        } else {
            return next.shouldContinuePropagation(frame, self, sourceId, lastNode);
        }
    }
}

class PropagationCachedNode extends ShouldContinuePropagationNode {


    private final int idxOfSource;
    private final long sourceId;
    private final long numSources;
    @Child
    HandlePropagation execAndPropagate;
    @Child
    ShouldContinuePropagationNode next;

    public PropagationCachedNode(RubyContext context, SourceSection section, ShouldContinuePropagationNode propNode, int idxOfSource, long sourceId, long numSources) {
        super(context, section);
        this.next = propNode;
        this.idxOfSource = idxOfSource;
        this.sourceId = sourceId;
        this.numSources = numSources;
        execAndPropagate = new HandlePropagation(context, section);
    }


    @Override
    public boolean shouldContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode) {
        if (self.getSourceToSelfPathCount().length > idxOfSource
                && sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
            final int count = self.getCount() + 1;
            if (count >= self.getSourceToSelfPathCount()[idxOfSource][1]) {
                //execAndPropagate.execute(frame,self,args);
                return true;
            } else {
                self.setCount(count);
                return false;
            }
        } else {
            return next.shouldContinuePropagation(frame, self, sourceId, lastNode);
        }
    }
}

class PropagationPolymorphNode extends ShouldContinuePropagationNode {
    @Child
    HandlePropagation execAndPropagate;


    public PropagationPolymorphNode(RubyContext context, SourceSection section) {
        super(context, section);
        execAndPropagate = new HandlePropagation(context, section);
    }

    @Override
    public boolean shouldContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode) {
        final long[][] souceToPath = self.getSourceToSelfPathCount();
        for (int i = 0; i < souceToPath.length; i++) {
            if (souceToPath[i][0] == sourceId) {
                final int count = self.getCount() + 1;
                if (count == souceToPath[i][1]) {
                    //execAndPropagate.execute(frame,self,args);
                    return true;
                } else {
                    self.setCount(count);
                    return false;
                }
            }
        }
        return false; //TODO if that is reached something went terrible wrong
    }
}

class PropagationUninitializedNode extends ShouldContinuePropagationNode {
    static final int MAX_CHAIN_SIZE = 3;
    private int depth = 0;

    public PropagationUninitializedNode(RubyContext context, SourceSection section) {
        super(context, section);
    }


    @Override
    public boolean shouldContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        final long numSources = numPaths(self, sourceId);
        BehaviorPropagationHeadNode headNode = getHeadNode();
        ShouldContinuePropagationNode propNode = getHeadNode().propagationNode;


        ShouldContinuePropagationNode newPropNode;
        if (depth >= MAX_CHAIN_SIZE) {
            newPropNode = new PropagationPolymorphNode(context, getSourceSection());
        } else {
            if (self.isChain()) {
                newPropNode = new PropagationSimpleCachedNode(context, getSourceSection(), propNode);
            } else {
                newPropNode = new PropagationCachedNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId, numSources);
            }
            depth += 1;
        }
        propNode.replace(newPropNode);
        return newPropNode.shouldContinuePropagation(frame, self, sourceId, lastNode);
    }

    private long numPaths(BehaviorObject self, long sourceId) {
        for (int i = 0; i < self.getSourceToSelfPathCount().length; i++) {
            if (sourceId == self.getSourceToSelfPathCount()[i][0]) {
                return self.getSourceToSelfPathCount()[i][1];
            }
        }
        return -1;
    }
}