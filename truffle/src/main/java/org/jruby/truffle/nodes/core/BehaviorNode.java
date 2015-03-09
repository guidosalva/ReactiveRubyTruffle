package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.literal.ObjectLiteralNode;
import org.jruby.truffle.nodes.objects.*;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

/**
 * Created by me on 26.02.15.
 */

/*
    Some part of the Behavior class are implemented in behavior.rb
 */
@CoreClass(name = "Behavior")
public abstract class BehaviorNode {



    @CoreMethod(names = "startOrdering")
    public abstract static class StartOrderingNode extends CoreMethodNode {
        @Child
        private CallDispatchHeadNode callSignalThatDependOnSelf;

        public StartOrderingNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callSignalThatDependOnSelf = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        public StartOrderingNode(StartOrderingNode prev) {
            super(prev);
            callSignalThatDependOnSelf = prev.callSignalThatDependOnSelf;
        }

        @Specialization
        public long startOrdering(VirtualFrame frame, SignalRuntime obj) {
            final SignalRuntime[] signals = obj.getSignalsThatDependOnSelf();

            //TODO check how truffle handles static attributs
            final long sigPropId = obj.getSigPropId() +1;
            obj.setSigPropId(sigPropId);
            for (SignalRuntime s : signals) {
                if (s != null)
                    callSignalThatDependOnSelf.call(frame, s, "ordering", null, sigPropId);
            }
            return sigPropId;
        }
    }
    @CoreMethod(names = "ordering", required = 1)
    public abstract static class OrderingNode extends CoreMethodNode{

        @Child
        private CallDispatchHeadNode callSigDepOrdering;


        public OrderingNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callSigDepOrdering = DispatchHeadNodeFactory.createMethodCall(context);
        }

        public OrderingNode(OrderingNode prev) {
            super(prev);
            callSigDepOrdering = prev.callSigDepOrdering;
        }

        @Specialization
        public int ordering(VirtualFrame frame, SignalRuntime obj, long sigPropId){
            if(obj.getCurSigPropId() == sigPropId){
                obj.setCurSigPropCount(obj.getCurSigPropCount() +1 );
            }else{
                obj.setCurSigPropCount(1);
                obj.setCurSigPropId(sigPropId);
            }
            final SignalRuntime[] signals = obj.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {
                if (s != null) {
                    callSigDepOrdering.call(frame, s, "ordering", null, sigPropId);
                }
            }
            return obj.getCurSigPropCount();
        }
    }

    @CoreMethod(names = "startUpdatePropagation", required = 0)
    public abstract static class StartUpdatePropagationNode extends CoreMethodNode {
        @Child
        private CallDispatchHeadNode callSignalThatDependOnSelf;
        @Child CallDispatchHeadNode callSigDepOrdering;

        public StartUpdatePropagationNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callSigDepOrdering = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
            callSignalThatDependOnSelf = DispatchHeadNodeFactory.createMethodCall(context, true);
            return;
        }

        public StartUpdatePropagationNode(StartUpdatePropagationNode prev) {
            super(prev);
            callSignalThatDependOnSelf = prev.callSignalThatDependOnSelf;
            callSigDepOrdering = prev.callSigDepOrdering;
            return;
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime obj) {
            callSigDepOrdering.call(frame, obj,"startOrdering", null,new Object[0]);
            final SignalRuntime[] signals = obj.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {
                if (s != null) {
                    callSignalThatDependOnSelf.call(frame, s, "update", null, obj.getSourceInfo());
                }
            }
            return obj;
        }
    }

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
            obj.setNumSourceChanges(obj.getNumSourceChanges() + 1);
            final int numChanges = obj.getNumSourceChanges();
            final int numPropPaths = obj.getCurSigPropCount();
            if (numChanges >= numPropPaths) {
                //we only update the expr if in the current propagation run we will not recive any more updats
                updateSelf.call(frame, obj, "execSigExpr", null, new Object[0]);
            }
            // we always need to call all siganlThatDepend on self so that they can calc the correct number of numchanges
            final SignalRuntime[] signals = obj.getSignalsThatDependOnSelf();
            for (SignalRuntime s : signals) {
                if (s != null)
                    callSignalThatDependOnSelf.call(frame, s, "update", null, data);
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
