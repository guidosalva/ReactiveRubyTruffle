package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.literal.ObjectLiteralNode;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

/**
 * Created by me on 26.02.15.
 */

/*
    Some part of the Behavior class are implemented in behavior.rb
 */
@CoreClass(name = "Behavior")
public abstract class BehaviorNode {

    @CoreMethod(names = "update", required = 1)
    public abstract static class UpdateNode extends CoreMethodNode {

        @Child
        private CallDispatchHeadNode updateSelf;
        @Child
        private CallDispatchHeadNode callSignalThatDependOnSelf;

        public UpdateNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            updateSelf = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
            callSignalThatDependOnSelf = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        public UpdateNode(UpdateNode prev) {
            super(prev);
            updateSelf = prev.updateSelf;
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime obj, Object data) {
            updateSelf.call(frame, obj, "execSigExpr", null, new Object[0]);
            final SignalRuntime[] signals =obj.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {
                if (s != null)
                    callSignalThatDependOnSelf.call(frame, s, "update", null, data);
            }
            return obj;
        }
    }

    @CoreMethod(names = "startUpdatePropagation", required = 0)
    public abstract static class StartUpdatePropagationNode extends CoreMethodNode {
        @Child
        private CallDispatchHeadNode callSignalThatDependOnSelf;
        ;

        public StartUpdatePropagationNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callSignalThatDependOnSelf = DispatchHeadNodeFactory.createMethodCall(context, true);
            return;
        }

        public StartUpdatePropagationNode(StartUpdatePropagationNode prev) {
            super(prev);
            callSignalThatDependOnSelf = prev.callSignalThatDependOnSelf;
            return;
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime obj) {
            final SignalRuntime[] signals =obj.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {
                if (s != null) {
                  callSignalThatDependOnSelf.call(frame, s, "update", null, obj.getSourceInfo());
                }
            }
            return obj;
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
            // System.out.println("now double")
            ;
            double value = readValue.executeFloat(frame);
            SignalRuntime outerSignalRuntime = readOuterSignal.executeSignalRuntime(frame);
            obj.addSignalThatDependsOnSelf(outerSignalRuntime);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        Object valueObject(VirtualFrame frame, SignalRuntime obj) throws UnexpectedResultException {
            // System.out.println("now object");
            Object value = readValue.execute(frame);
            SignalRuntime outerSignalRuntime = readOuterSignal.executeSignalRuntime(frame);
            obj.addSignalThatDependsOnSelf(outerSignalRuntime);
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
