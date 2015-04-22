package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.behavior.BehaviorOption;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

/**
 * Created by me on 16.03.15.
 */
//TODO remove and move the functionality in the normal Behavior node. To reduce code duplication
@CoreClass(name = "BehaviorSource")
public class BehaviorSource {

    @CoreMethod(names = "initialize", needsBlock = false, required = 1)
    public abstract static class InitializeArity1Node extends CoreMethodNode {


        @Child
        private WriteHeadObjectFieldNode writeValue;

        public InitializeArity1Node(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        }


        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, int value) {
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, long value) {
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, double value) {
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object value) {
            writeValue.execute(self, value);
            return self;
        }


    }

    @CoreMethod(names = "emit", needsBlock = true, required = 1)
    public abstract static class EmitNode extends CoreMethodNode {

        @Child
        private WriteHeadObjectFieldNode writeValue;

        @Child CallDispatchHeadNode callPropagationSelf;
        @Child CallDispatchHeadNode callPropagationOtherSources;
        @Child StartPropagationNode propagationNode;


        public EmitNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            callPropagationSelf = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
            callPropagationOtherSources = DispatchHeadNodeFactory.createMethodCall(context);
            propagationNode = new StartPropagationNode(context,sourceSection);
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, int value) {
            writeValue.execute(self, value);
            startPropagation(frame, self,value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, long value) {
            writeValue.execute(self, value);
            startPropagation(frame, self,value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, double value) {
            writeValue.execute(self, value);
            startPropagation(frame, self,value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object value) {
            writeValue.execute(self, value);
            startPropagation(frame, self,value);
            return self;
        }

        private void startPropagation(VirtualFrame frame, SignalRuntime self, Object value){
            //self.getId()
            propagationNode.startPropagation(frame,self,BehaviorOption.createBehaviorPropagationArgs(self.getId(),self));
        }
    }



    public static class StartPropagationNode extends Node {

        @Child
        CallDispatchHeadNode updateNode;
        @Child
        ReadInstanceVariableNode readValue;

        public StartPropagationNode(RubyContext context, SourceSection sourceSection) {
            updateNode = DispatchHeadNodeFactory.createMethodCall(context, true);
            readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
        }

        public SignalRuntime startPropagation(VirtualFrame frame, SignalRuntime self, Object[] args) {
            final SignalRuntime[] sigs = self.getSignalsThatDependOnSelf();
            for (int i = 0; i < sigs.length; i++) {
                updateNode.call(frame, sigs[i], "propagation", null, args);
            }
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
}
