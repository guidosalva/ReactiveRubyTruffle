package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.Ruby;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.dispatch.DispatchNode;
import org.jruby.truffle.nodes.literal.ObjectLiteralNode;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyArguments;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.UndefinedPlaceholder;
import org.jruby.truffle.runtime.control.RaiseException;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

import java.util.*;

/**
 * Created by me on 26.02.15.
 */

/*
    Some part of the Behavior class are implemented in behavior.rb
 */
@CoreClass(name = "BehaviorSimple")
public abstract class BehaviorSimpleNode extends BehaviourSuper {

    @CoreMethod(names = "initialize", needsBlock = true, argumentsAsArray = true)
    public abstract static class InitializeNode extends CoreMethodNode {

        @Child
        private WriteHeadObjectFieldNode writeSignalExpr;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode("@sigExpr");
        }

        public InitializeNode(InitializeNode prev) {
            super(prev);
            writeSignalExpr = prev.writeSignalExpr;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object[] dependsOn, RubyProc signalExp) {
            if (dependsOn[0] instanceof SignalRuntime) {
                initializedSignal(self, dependsOn);
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new RuntimeException("args need to be object array of signals");
            }
            writeSignalExpr.execute(self, signalExp);
            return self;
        }

        @CompilerDirectives.TruffleBoundary
        private void initializedSignal(SignalRuntime self, Object[] dependsOn) {
            final SignalRuntime[] desp = new SignalRuntime[dependsOn.length];
            for (int i = 0; i < dependsOn.length; i++) {
                desp[i] = (SignalRuntime) dependsOn[i];
                ((SignalRuntime) dependsOn[i]).addSignalThatDependsOnSelf(self);
            }
            Map<Long, Long> source = new HashMap<>();
            for (int i = 0; i < desp.length; i++) {
                final long[][] sourceToSelfPathCount = desp[i].getSourceToSelfPathCount();
                if (sourceToSelfPathCount != null) {
                    for (int j = 0; j < sourceToSelfPathCount.length; j++) {
                        long key = sourceToSelfPathCount[j][0];
                        long value = sourceToSelfPathCount[j][1];
                        if (source.containsKey(key)) {
                            source.put(key, value + 1);
                        } else {
                            source.put(key, value);
                        }
                    }
                }else{
                    //we ahve a source
                    source.put(desp[i].getId(),(long)1);
                }
            }
            final long[][] newSourceToSelfPathCount = new long[source.size()][];
            int idx = 0;
            ArrayList<Long> keys = new ArrayList<>(source.keySet());
            java.util.Collections.sort(keys);
            for (long key : keys) {
                newSourceToSelfPathCount[idx] = new long[2];
                newSourceToSelfPathCount[idx][0] = key;
                newSourceToSelfPathCount[idx][1] = source.get(key);
                idx += 1;
            }
            self.setSourceToSelfPathCount(newSourceToSelfPathCount);
        }
    }

    public static class ExecSignalExprNode extends Node {

        @Child
        ReadInstanceVariableNode readSigExpr;
        @Child
        private WriteHeadObjectFieldNode value;

        @Child
        private YieldDispatchHeadNode dispatchNode;

        private final Object args = new Object[0];

        public ExecSignalExprNode(RubyContext context, SourceSection sourceSection) {
            dispatchNode = new YieldDispatchHeadNode(context);
            value = new WriteHeadObjectFieldNode(VALUE_VAR);
            readSigExpr = new ReadInstanceVariableNode(context, sourceSection, "@sigExpr", new SelfNode(context, sourceSection), false);
        }


        public Object execSigExpr(VirtualFrame frame, SignalRuntime self) {
            try {
                final RubyProc proc = readSigExpr.executeRubyProc(frame);
                value.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
            } catch (UnexpectedResultException e) {
                //TODO something that makes sense i need to do
            }
            return self;
        }


    }


    @CoreMethod(names = "propagation", required = 1)
    public abstract static class PropagationMethodNode extends CoreMethodNode {
        //        @Child
//        private CallDispatchHeadNode callDependentSignals;
//        @Child
//        ReadInstanceVariableNode readValue;
//        @Child
//        ExecSignalExprNode execSigExpr;
        @Child
        PropagationHeadNode propNode;

        public PropagationMethodNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
//            callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
//            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
//            execSigExpr = new ExecSignalExprNode(context,sourceSection);
            propNode = new PropagationHeadNode(context, sourceSection);
        }

        public PropagationMethodNode(PropagationMethodNode prev) {
            super(prev);
//            callDependentSignals = prev.callDependentSignals;
//            readValue = prev.readValue;
//            execSigExpr = prev.execSigExpr;
            propNode = prev.propNode;
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime self, long source) {
            propNode.handlePropagation(frame, self, source);
            return self;
//            execSigExpr.execSigExpr(frame,self);
//            final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
//            for (SignalRuntime s : signals) {
//                callDependentSignals.call(frame, s, "propagation", null, new Object[0]);
//            }
//            return self;
        }
    }

    public static class PropagationHeadNode extends Node {
        @Child
        PropagationNode propagationNode;

        public PropagationHeadNode(RubyContext context, SourceSection section) {
            super(section);
            propagationNode = new PropagationUninitializedNode(context, section);
        }

        public void handlePropagation(VirtualFrame frame, SignalRuntime self, long sourceId) {
            propagationNode.propagate(frame, self, sourceId);
        }


    }

    public abstract static class PropagationNode extends Node {
        protected final RubyContext context;

        public PropagationNode(RubyContext context, SourceSection section) {
            super(section);
            this.context = context;
        }


        public abstract void propagate(VirtualFrame frame, SignalRuntime self, long sourceId);

        protected PropagationHeadNode getHeadNode() {
            return NodeUtil.findParent(this, PropagationHeadNode.class);
        }


    }

    public static class PropagationSimpleCachedNode extends PropagationNode {

        private final int idxOfSource;
        private final long sourceId;

        @Child
        PropagationNode next;
        @Child
        ExecSignalExprNode execSigExpr;
        @Child
        CallDispatchHeadNode callDependentSignals;

        public PropagationSimpleCachedNode(RubyContext context, SourceSection section, PropagationNode next, int idxOfSource, long sourceId) {
            super(context, section);
            this.idxOfSource = idxOfSource;
            this.sourceId = sourceId;
            this.next = next;
            this.execSigExpr = new ExecSignalExprNode(context, section);
            callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        @Override
        public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
            if (sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
                execSigExpr.execSigExpr(frame, self);
                self.setCount(0);
                final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
                for (SignalRuntime s : signals) {
                    callDependentSignals.call(frame, s, "propagation", null, sourceId);
                }
            } else {
                next.propagate(frame, self, sourceId);
            }
        }
    }

    public static class PropagationCachedNode extends PropagationNode {


        private final int idxOfSource;
        private final long sourceId;
        private final long numSources;
        @Child
        PropagationNode next;
        @Child
        ExecSignalExprNode execSigExpr;
        @Child
        CallDispatchHeadNode callDependentSignals;

        public PropagationCachedNode(RubyContext context, SourceSection section, PropagationNode propNode, int idxOfSource, long sourceId, long numSources) {
            super(context, section);
            this.next = propNode;
            this.execSigExpr = new ExecSignalExprNode(context, section);
            this.idxOfSource = idxOfSource;
            this.sourceId = sourceId;
            this.numSources = numSources;
            callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        @Override
        public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
            if (sourceId == self.getSourceToSelfPathCount()[idxOfSource][0]) {
                int count = self.getCount() + 1;
                if (count == numSources) {
                    execSigExpr.execSigExpr(frame, self);
                    self.setCount(0);
                    final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
                    for (SignalRuntime s : signals) {
                        callDependentSignals.call(frame, s, "propagation", null, sourceId);
                    }
                } else {
                    self.setCount(count);
                }
            } else {
                next.propagate(frame, self, sourceId);
            }
        }
    }

    public static class PropagationPolymorphNode extends PropagationNode {

        public PropagationPolymorphNode(RubyContext context, SourceSection section) {
            super(context, section);
        }

        @Override
        public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new RuntimeException("not implemented yet");
        }
    }

    public static class PropagationUninitializedNode extends PropagationNode {
        static final int MAX_CHAIN_SIZE = 3;
        private int depth = 0;

        public PropagationUninitializedNode(RubyContext context, SourceSection section) {
            super(context, section);
        }


        @Override
        public void propagate(VirtualFrame frame, SignalRuntime self, long sourceId) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            final long numSources = numPaths(self, sourceId);
            PropagationHeadNode headNode = getHeadNode();
            PropagationNode propNode = getHeadNode().propagationNode;


            PropagationNode newPropNode;
            if (depth == MAX_CHAIN_SIZE) {
                newPropNode = new PropagationPolymorphNode(context, getSourceSection());
            } else {
                if (numSources == 1) {
                    newPropNode = new PropagationSimpleCachedNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId);
                } else {
                    newPropNode = new PropagationCachedNode(context, getSourceSection(), propNode, self.getIdxOfSource(sourceId), sourceId, numSources);
                }
            }
            propNode.replace(newPropNode);
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


    @CoreMethod(names = "value")
    public abstract static class ValueNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
        }

        public ValueNode(ValueNode prev) {
            super(prev);
            readValue = prev.readValue;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        int valueInt(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            int value = readValue.executeIntegerFixnum(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        long valueLong(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            long value = readValue.executeLongFixnum(frame);
            return value;
        }


        @Specialization(rewriteOn = UnexpectedResultException.class)
        double valueDouble(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            double value = readValue.executeFloat(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        Object valueObject(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            // System.out.println("now object");
            Object value = readValue.execute(frame);
            return value;
        }

        static boolean isInt(SignalRuntime s) {
            return true;
        }

        static boolean isDouble(SignalRuntime s) {
            return false;
        }

        static boolean isNotPrimitve(SignalRuntime s) {
            return !isInt(s) && !isDouble(s);
        }

    }

    @CoreMethod(names = "now")
    public abstract static class NowNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;

        public NowNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
        }

        public NowNode(NowNode prev) {
            super(prev);
            readValue = prev.readValue;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        int nowInt(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            int value = readValue.executeIntegerFixnum(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        long nowLong(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            long value = readValue.executeIntegerFixnum(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        double nowDouble(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            double value = readValue.executeFloat(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        Object nowObject(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            Object value = readValue.execute(frame);
            return value;
        }

        static boolean isInt(SignalRuntime s) {
            return true;
        }

        static boolean isDouble(SignalRuntime s) {
            return false;
        }

        static boolean isNotPrimitve(SignalRuntime s) {
            return !isInt(s) && !isDouble(s);
        }

    }
}
