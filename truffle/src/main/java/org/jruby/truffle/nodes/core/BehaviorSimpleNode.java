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
public abstract class BehaviorSimpleNode {


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
            }else{
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

//    @CoreMethod(names = "initialize", needsBlock = true, required = 1)
//    public abstract static class InitializeArity1Node extends CoreMethodNode{
//
//        @Child private WriteHeadObjectFieldNode writeSignalExpr;
////        @Child private CallDispatchHeadNode callSigExp;
//
//        public InitializeArity1Node(RubyContext context, SourceSection sourceSection) {
//            super(context, sourceSection);
//            writeSignalExpr = new WriteHeadObjectFieldNode("@sigExpr");
////            callSigExp = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
//        }
//
//        public InitializeArity1Node(InitializeArity1Node prev) {
//            super(prev);
//            writeSignalExpr = prev.writeSignalExpr;
////            callSigExp = prev.callSigExp;
//        }
//
//        @Specialization
//        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, SignalRuntime dependsOn, RubyProc signalExp){
//            dependsOn.addSignalThatDependsOnSelf(self);
//            SignalRuntime[] depOn = new SignalRuntime[1];
//            depOn[0] = dependsOn;
//            self.setValues(new Object[1]);
//            self.setSelfDependsOn(depOn);
//            writeSignalExpr.execute(self,signalExp);
//            return self;
//        }
//    }

    /*
        def execSimpleSignal(value)
        @value = sigExpr.call(value)
    end
     */
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
        public Object execSigExpr(VirtualFrame frame, SignalRuntime self, Object[] args) {
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
        Object update(VirtualFrame frame, SignalRuntime self, Object[] args) {
            final SignalRuntime source = (SignalRuntime) args[1];

            final boolean needToChange = ((boolean) args[0]) || self.getNeedToChange();
            self.setNeedToChange(needToChange);

            self.getValues()[getPos(self, source)] = args[2];

            final boolean allSourcesEmitted = allNecessaryValues(self);

            if (allSourcesEmitted && needToChange) {
                callSignalExpr.call(frame, self, "execSimpleSignal", null, self.getValues());
            }
            if (allSourcesEmitted) {
                final SignalRuntime[] signals = self.getSignalsThatDependOnSelf();
                for (SignalRuntime s : signals) {
                    Object[] newArgs = new Object[3];
                    newArgs[0] = needToChange;
                    newArgs[1] = self;
                    newArgs[2] = readValue.execute(frame);
                    callDependentSignals.call(frame, s, "propagation", null, newArgs);
                }
            }
            return self;
        }

        public int getPos(SignalRuntime self, SignalRuntime source) {
            for (int i = 0; i < self.getSelfDependsOn().length; i++) {
                if (self.getSelfDependsOn()[i].equals(source))
                    return i;
            }
            return -1;
        }

        public boolean allNecessaryValues(SignalRuntime self) {
            int count = 0;
            for (int i = 0; i < self.getSelfDependsOn().length; i++) {
                if (self.getValues()[i] == null) {
                    count += 1;
                }
            }
            return count == 0;
        }

        public void removedChachedValues(SignalRuntime self) {
            for (int i = 0; i < self.getSelfDependsOn().length; i++) {
                self.getValues()[i] = null;
            }
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
            readValue = new ReadInstanceVariableNode(context, sourceSection, "@value", new SelfNode(context, sourceSection), false);
            readOuterSignal = new ReadInstanceVariableNode(context, sourceSection, "$outerSignalHack", new ObjectLiteralNode(context, sourceSection, context.getCoreLibrary().getGlobalVariablesObject()), true);
        }

        public ValueNode(ValueNode prev) {
            super(prev);
            readValue = prev.readValue;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        int valueInt(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            // System.out.println("now int");
            int value = readValue.executeIntegerFixnum(frame);
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
}
