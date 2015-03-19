package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.literal.ObjectLiteralNode;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyArguments;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyArray;
import org.jruby.truffle.runtime.core.RubyException;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

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
//        @Child private CallDispatchHeadNode callSigExp;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode("@sigExpr");
//            callSigExp = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
        }

        public InitializeNode(InitializeNode prev) {
            super(prev);
            writeSignalExpr = prev.writeSignalExpr;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object[] dependsOn, RubyProc signalExp) {
            if (dependsOn[0] instanceof SignalRuntime) {
                final SignalRuntime[] depOn = new SignalRuntime[dependsOn.length];
                for (int i = 0; i < depOn.length; i++) {
                    depOn[i] = (SignalRuntime) dependsOn[i];
                    depOn[i].addSignalThatDependsOnSelf(self);
                }
                self.setSelfDependsOn(depOn);
                self.setValues(new Object[dependsOn.length]);
            } else {
                RubyArray array = (RubyArray) dependsOn[0];
                Object[] store = (Object[]) array.getStore();
                final SignalRuntime[] depOn = new SignalRuntime[store.length];
                for (int i = 0; i < store.length; i++) {
                    ((SignalRuntime) store[i]).addSignalThatDependsOnSelf(self);
                    depOn[i] = (SignalRuntime) store[i];
                }
                self.setSelfDependsOn(depOn);
                self.setValues(new Object[store.length]);
            }
            writeSignalExpr.execute(self, signalExp);
            return self;
        }
    }

    @CoreMethod(names = "execSimpleSignal", argumentsAsArray = true)
    public abstract static class ExecSignalExprNode extends YieldingCoreMethodNode {

        @Child
        ReadInstanceVariableNode readSigExpr;
        @Child
        private WriteHeadObjectFieldNode value;
        private final Object args = new Object[0];

        public ExecSignalExprNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            value = new WriteHeadObjectFieldNode("@value");
            readSigExpr = new ReadInstanceVariableNode(context, sourceSection, "@sigExpr", new SelfNode(context, sourceSection), false);
        }

        public ExecSignalExprNode(ExecSignalExprNode prev) {
            super(prev);
            readSigExpr = prev.readSigExpr;
            value = prev.value;
        }

        @Specialization
        public Object execSigExpr(VirtualFrame frame, SignalRuntime self) {
            try {
                final RubyProc proc = readSigExpr.executeRubyProc(frame);
                value.execute(self, yield(frame, proc, args));
            } catch (UnexpectedResultException e) {
                //TODO something that makes sense i need to do
            }


            return self;
        }


    }


    @CoreMethod(names = "propagation", argumentsAsArray = true)
    public abstract static class PropagationNode extends CoreMethodNode {
        @Child
        private CallDispatchHeadNode callDependentSignals;
        @Child
        CallDispatchHeadNode callSignalExpr;
        @Child
        ReadInstanceVariableNode readValue;

        public PropagationNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callSignalExpr = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
            callDependentSignals = DispatchHeadNodeFactory.createMethodCall(context, true);
            readValue = new ReadInstanceVariableNode(context, sourceSection, "@value", new SelfNode(context, sourceSection), false);
        }

        public PropagationNode(PropagationNode prev) {
            super(prev);
            callDependentSignals = prev.callDependentSignals;
            callSignalExpr = prev.callSignalExpr;
            readValue = prev.readValue;

        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime self) {

            callSignalExpr.call(frame, self, "execSimpleSignal", null, new Object[0]);
            final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {

                callDependentSignals.call(frame, s, "propagation", null, new Object[0]);
            }

            return self;
        }

    }

    @CoreMethod(names = "value")
    public abstract static class ValueNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;
        @Child
        ReadInstanceVariableNode readOuterSignal;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
            readOuterSignal = new ReadInstanceVariableNode(context, sourceSection, "$outerSignalHack", new ObjectLiteralNode(context, sourceSection, context.getCoreLibrary().getGlobalVariablesObject()), true);
        }

        public ValueNode(ValueNode prev) {
            super(prev);
            readValue = prev.readValue;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        int valueInt(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            int value = readValue.executeIntegerFixnum(frame);
            SignalRuntime outerSignalRuntime = readOuterSignal.executeSignalRuntime(frame);
            obj.addSignalThatDependsOnSelf(outerSignalRuntime);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        long valueLong(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            long value = readValue.executeLongFixnum(frame);
            SignalRuntime outerSignalRuntime = readOuterSignal.executeSignalRuntime(frame);
            obj.addSignalThatDependsOnSelf(outerSignalRuntime);
            return value;
        }


        @Specialization(rewriteOn = UnexpectedResultException.class)
        double valueDouble(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            double value = readValue.executeFloat(frame);
            SignalRuntime outerSignalRuntime = readOuterSignal.executeSignalRuntime(frame);
            obj.addSignalThatDependsOnSelf(outerSignalRuntime);
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
