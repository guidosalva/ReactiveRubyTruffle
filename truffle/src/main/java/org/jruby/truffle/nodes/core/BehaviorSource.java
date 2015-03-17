package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.methods.arguments.ReadAllArgumentsNode;
import org.jruby.truffle.nodes.objects.WriteInstanceVariableNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

/**
 * Created by me on 16.03.15.
 */
@CoreClass(name = "BehaviorSource")
public class BehaviorSource {

    @CoreMethod(names = "startPropagation", needsSelf = true)
    public abstract static class StartPropagationNode extends CoreMethodNode {

        @Child
        CallDispatchHeadNode updateNode;

        public StartPropagationNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            updateNode = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        public StartPropagationNode(StartPropagationNode prev) {
            super(prev);
            updateNode = prev.updateNode;
        }

        @Specialization
        public SignalRuntime startPropagation(VirtualFrame frame, SignalRuntime self){
            final SignalRuntime[] sigs = self.getSignalsThatDependOnSelf();
            for(int i = 0; i < sigs.length ; i++){
                updateNode.call(frame, sigs[i], "propagation", null, new Object[0]);
            }
            return self;
        }
    }

    @CoreMethod(names = "addDependentSignal", required = 1, needsSelf = true)
    public abstract  static class AddDependentSignalNode extends  CoreMethodNode{

        public AddDependentSignalNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public AddDependentSignalNode(AddDependentSignalNode prev) {
            super(prev);
        }

        @Specialization
        public SignalRuntime addDependentSignal(SignalRuntime self, SignalRuntime signalObject){
            self.addSignalThatDependsOnSelf(signalObject);
            return signalObject;
        }
    }
}
