package org.jruby.truffle.nodes.core.behavior.propagation;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.BehaviorObject;


public abstract class GlitchFreedomCheckNode extends Node {
    protected final RubyContext context;

    public GlitchFreedomCheckNode(RubyContext context, SourceSection section) {
        super(section);
        this.context = context;
    }

    public static GlitchFreedomCheckNode createUninitializedShouldPropagationNode(RubyContext context, SourceSection section) {
        return new PropagationUninitializedNode(context, section);
    }

    public abstract boolean canContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId);

    @CompilerDirectives.TruffleBoundary
    protected BehaviorPropagationHeadNode getHeadNode() {
        return NodeUtil.findParent(this, BehaviorPropagationHeadNode.class);
    }


}

class ChainGlitchFreedomNode extends GlitchFreedomCheckNode {
    @Child
    protected GlitchFreedomCheckNode next;

    public ChainGlitchFreedomNode(RubyContext context, SourceSection section, GlitchFreedomCheckNode next) {
        super(context, section);
        this.next = next;
    }

    @Override
    public boolean canContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId) {
        if (self.isChain()) {
            return true;
        } else {
            return next.canContinuePropagation(frame, self, sourceId);
        }
    }
}

class NonChainGlitchFreedomNode extends GlitchFreedomCheckNode {


    private final int idxOfSource;
    @Child
    protected GlitchFreedomCheckNode next;

    public NonChainGlitchFreedomNode(RubyContext context, SourceSection section, GlitchFreedomCheckNode propNode, int idxOfSource, long sourceId, long numSources) {
        super(context, section);
        this.next = propNode;
        this.idxOfSource = idxOfSource;
    }


    @Override
    public boolean canContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId) {
        if (self.sourceToSelfSize() > idxOfSource
                && sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
            if (self.getCount() >= self.getSourceToSelfPathCount()[idxOfSource][1]) {
                return true;
            } else {
                final int count = self.getCount() + 1;
                self.setCount(count);
                return false;
            }
        } else {
            return next.canContinuePropagation(frame, self, sourceId);
        }
    }
}

class PropagationPolymorphNode extends GlitchFreedomCheckNode {
    @Child
    protected HandlePropagation execAndPropagate;


    public PropagationPolymorphNode(RubyContext context, SourceSection section) {
        super(context, section);
        execAndPropagate = new HandlePropagation(context, section);
    }

    @Override
    public boolean canContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId) {
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

class PropagationUninitializedNode extends GlitchFreedomCheckNode {
    static final int MAX_CHAIN_SIZE = 3;
    private int depth = 0;

    public PropagationUninitializedNode(RubyContext context, SourceSection section) {
        super(context, section);
    }


    @Override
    public boolean canContinuePropagation(VirtualFrame frame, BehaviorObject self, long sourceId) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        final long numSources = numPaths(self, sourceId);
        GlitchFreedomCheckNode propNode = getHeadNode().propagationNode;


        GlitchFreedomCheckNode newPropNode;
        if (depth >= MAX_CHAIN_SIZE) {
            newPropNode = new PropagationPolymorphNode(context, getSourceSection());
        } else {
            if (self.isChain()) {
                newPropNode = new ChainGlitchFreedomNode(context, getSourceSection(), propNode);
            } else {
                newPropNode = new NonChainGlitchFreedomNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId, numSources);
            }
            depth += 1;
        }
        propNode.replace(newPropNode);
        return newPropNode.canContinuePropagation(frame, self, sourceId);
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