package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.runtime.Visibility;
import org.jruby.truffle.nodes.behavior.BehaviorPropagationHeadNode;
import org.jruby.truffle.nodes.behavior.BehaviorOption;
import org.jruby.truffle.nodes.behavior.HandleBehaviorExprInitializationNode;
import org.jruby.truffle.nodes.behavior.HandleBehaviorExprNode;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.*;
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
        @Child
        HandleBehaviorExprInitializationNode execSignalExpr;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            execSignalExpr = new HandleBehaviorExprInitializationNode(context, sourceSection);
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object[] dependsOn, RubyProc signalExp) {
            if (dependsOn[0] instanceof SignalRuntime) {
                self.setupPropagationDep(dependsOn);
                writeSignalExpr.execute(self, signalExp);
                execSignalExpr.execute(frame, self,dependsOn);
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new RuntimeException("args need to be object array of signals");
            }
            return self;
        }
    }


    @CoreMethod(names = "propagation", required = 2, visibility = Visibility.PRIVATE)
    public abstract static class PropagationMethodNode extends CoreMethodNode {
        @Child
        BehaviorPropagationHeadNode propNode;

        public PropagationMethodNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            propNode = new BehaviorPropagationHeadNode(context, sourceSection);
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime self, long sourceId, SignalRuntime lastNode) {
            propNode.execute(frame, self, sourceId,lastNode);
            return self;
        }




    }


    @CoreMethod(names = "value")
    public abstract static class ValueNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
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


    @CoreMethod(names = "fold", required = 1, needsBlock = true  )
    public abstract static class FoldNode extends CoreMethodNode {

        @Child
        WriteHeadObjectFieldNode writeFoldValue;
        @Child
        WriteHeadObjectFieldNode writeFoldFunction;

        public FoldNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        }

        @Specialization
        public SignalRuntime fold(VirtualFrame frame, SignalRuntime self, int init, RubyProc proc){
            SignalRuntime newSignal = SignalRuntime.newFoldSignal(self, getContext());
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public SignalRuntime fold(SignalRuntime self, long init, RubyProc proc){
            SignalRuntime newSignal = SignalRuntime.newFoldSignal(self, getContext());
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public SignalRuntime fold(SignalRuntime self, double init, RubyProc proc){
            SignalRuntime newSignal = SignalRuntime.newFoldSignal(self, getContext());
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public SignalRuntime fold(SignalRuntime self, Object init, RubyProc proc){
            SignalRuntime newSignal = SignalRuntime.newFoldSignal(self,getContext());
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
    }

    @CoreMethod(names = "foldN", argumentsAsArray = true, needsBlock = true  )
    public abstract static class FoldNNode extends CoreMethodNode {

        @Child
        WriteHeadObjectFieldNode writeFoldValue;
        @Child
        WriteHeadObjectFieldNode writeFoldFunction;

        public FoldNNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        }

        @Specialization
        public SignalRuntime fold(VirtualFrame frame,SignalRuntime self, Object[] args, RubyProc proc){
            Object value = args[0];
            SignalRuntime[] deps = new SignalRuntime[args.length];
            deps[0] = self;
            for(int i = 1; i < args.length; i++){
                deps[i] = (SignalRuntime) args[i];
            }
            SignalRuntime newSignal = SignalRuntime.newFoldSignal(deps, getContext());
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,value);
            return newSignal;
        }
    }

    @CoreMethod(names = "onChange")
    public abstract static class OnChangeNode extends CoreMethodNode {
        public OnChangeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "remove")
    public abstract static class RemoveNode extends CoreMethodNode {
        public RemoveNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "filter")
    public abstract static class FilterNode extends CoreMethodNode {
        public FilterNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "merge")
    public abstract static class MergeNode extends CoreMethodNode {
        public MergeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "map")
    public abstract static class MapNode extends CoreMethodNode {

        public MapNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "take")
    public abstract static class TakeNode extends CoreMethodNode {

        public TakeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "skip")
    public abstract  static class SkipNode extends CoreMethodNode {

        public SkipNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }


    //TODO the following method could be a problem
    // i may want to skip this method
    @CoreMethod(names = "delay")
    public abstract static class DelayNode extends CoreMethodNode {

        public DelayNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "throttle")
    public abstract  static class ThrottleNode extends CoreMethodNode {
        public ThrottleNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "bufferWithTime")
    public abstract static class BufferWithTime extends CoreMethodNode{

        public BufferWithTime(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }


    @CoreMethod(names = "now")
    public abstract static class NowNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;

        public NowNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
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
