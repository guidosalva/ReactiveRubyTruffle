package org.jruby.truffle.nodes.core;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.RubyCallNode;
import org.jruby.truffle.nodes.behavior.BehaviorOption;
import org.jruby.truffle.nodes.behavior.HandleBehaviorExprInitializationNode;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

@CoreClass(name = "BehaviorCore")
public class BehaviorModule {

    @CoreMethod(names = "map", isModuleFunction = true, argumentsAsArray = true, needsBlock = true)
    public abstract static class MapNode extends CoreMethodNode {
        @Child
        private WriteHeadObjectFieldNode writeSignalExpr;
        @Child
        HandleBehaviorExprInitializationNode execSignalExpr;


        public MapNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            execSignalExpr = new HandleBehaviorExprInitializationNode(context, sourceSection);

        }

        @Specialization
        SignalRuntime map(VirtualFrame frame, Object[] dependsOn, RubyProc block) {
            SignalRuntime self = newSignal();
            self.setupPropagationDep(dependsOn);
            writeSignalExpr.execute(self, block);
            execSignalExpr.execute(frame, self, dependsOn);
            return self;
        }

        @CompilerDirectives.TruffleBoundary
        private SignalRuntime newSignal() {
            return (SignalRuntime) (new SignalRuntime.SignalRuntimeAllocator()).allocate(getContext(), getContext().getCoreLibrary().getBehaviorSimpleclass(), null);
        }

    }

    @CoreMethod(names = "source", isModuleFunction = true, required = 1)
    public abstract static class SourceNode extends CoreMethodNode {
        @Child
        CallDispatchHeadNode callInit;

        public SourceNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callInit = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

        @Specialization
        SignalRuntime source(VirtualFrame frame, int value) {
            final SignalRuntime self = newSignal();
            return (SignalRuntime) callInit.call(frame, newSignal(), "initialize", null, value);
        }

        @Specialization
        SignalRuntime source(VirtualFrame frame, Object value) {
            final SignalRuntime self = newSignal();
            return (SignalRuntime) callInit.call(frame, newSignal(), "initialize", null, value);
        }

        @CompilerDirectives.TruffleBoundary
        private SignalRuntime newSignal() {
            return (SignalRuntime) (new SignalRuntime.SignalRuntimeAllocator()).allocate(getContext(), getContext().getCoreLibrary().getBehaviorSourceClass(), null);
        }

    }

}
