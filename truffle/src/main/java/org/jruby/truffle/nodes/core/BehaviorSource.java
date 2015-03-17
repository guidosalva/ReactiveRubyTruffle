package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.methods.arguments.ReadAllArgumentsNode;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objects.WriteInstanceVariableNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

/**
 * Created by me on 16.03.15.
 */
@CoreClass(name = "BehaviorSource")
public class BehaviorSource {

    @CoreMethod(names = "initialize", needsBlock = true, required = 1)
    public abstract static class InitializeArity1Node extends CoreMethodNode {

        @Child
        private WriteHeadObjectFieldNode writeValue;

        public InitializeArity1Node(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeValue = new WriteHeadObjectFieldNode("@value");
        }

        public InitializeArity1Node(InitializeArity1Node prev) {
            super(prev);
            writeValue = prev.writeValue;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, int value) {
            self.getSources().add(self);
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, long value) {
            self.getSources().add(self);
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, double value) {
            self.getSources().add(self);
            writeValue.execute(self, value);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object value) {
            self.getSources().add(self);
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


        public EmitNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeValue = new WriteHeadObjectFieldNode("@value");
            callPropagationSelf = DispatchHeadNodeFactory.createMethodCallOnSelf(context);
            callPropagationOtherSources = DispatchHeadNodeFactory.createMethodCall(context);
        }

        public EmitNode(EmitNode prev) {
            super(prev);
            writeValue = prev.writeValue;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, int value) {
            writeValue.execute(self, value);
            startPropatation(frame,self);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, long value) {
            writeValue.execute(self, value);
            startPropatation(frame,self);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, double value) {
            writeValue.execute(self, value);
            startPropatation(frame,self);
            return self;
        }

        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object value) {
            writeValue.execute(self, value);
            startPropatation(frame,self);
            return self;
        }

        private void startPropatation(VirtualFrame frame, SignalRuntime self){
            for(SignalRuntime s : self.getSources()){
                if(s.equals(self)){
                    callPropagationSelf.call(frame,self,"startPropagation",null,true);
                }else{
                    callPropagationSelf.call(frame,s,"startPropagation",null,false);
                }
            }

        }
    }


    @CoreMethod(names = "startPropagation", needsSelf = true, required = 1)
    public abstract static class StartPropagationNode extends CoreMethodNode {

        @Child
        CallDispatchHeadNode updateNode;
        @Child
        ReadInstanceVariableNode readValue;

        public StartPropagationNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            updateNode = DispatchHeadNodeFactory.createMethodCall(context, true);
            readValue = new ReadInstanceVariableNode(context, sourceSection, "@value", new SelfNode(context, sourceSection), false);
        }

        public StartPropagationNode(StartPropagationNode prev) {
            super(prev);
            updateNode = prev.updateNode;
            readValue = prev.readValue;
        }

        @Specialization
        public SignalRuntime startPropagation(VirtualFrame frame, SignalRuntime self,boolean changed) {
            final SignalRuntime[] sigs = self.getSignalsThatDependOnSelf();
            for (int i = 0; i < sigs.length; i++) {
                Object[] args = new Object[3];
                args[0] = changed;
                args[1] = self;
                args[2] = readValue.execute(frame);
                updateNode.call(frame, sigs[i], "propagation", null, args);
            }
            return self;
        }
    }

    @CoreMethod(names = "addDependentSignal", required = 1, needsSelf = true)
    public abstract static class AddDependentSignalNode extends CoreMethodNode {

        public AddDependentSignalNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public AddDependentSignalNode(AddDependentSignalNode prev) {
            super(prev);
        }

        @Specialization
        public SignalRuntime addDependentSignal(SignalRuntime self, SignalRuntime signalObject) {
            self.addSignalThatDependsOnSelf(signalObject);
            return signalObject;
        }
    }
}
