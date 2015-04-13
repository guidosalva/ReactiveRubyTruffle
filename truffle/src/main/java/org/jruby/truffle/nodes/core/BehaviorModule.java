package org.jruby.truffle.nodes.core;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

@CoreClass(name = "BehaviorCore")
public class BehaviorModule {

    @CoreMethod(names = "map", isModuleFunction = true, argumentsAsArray = true,needsBlock = true)
    public abstract static class MapNode extends CoreMethodNode{
        @Child
        CallDispatchHeadNode callInit;

        public MapNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callInit = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

//        public MapNode(MapNode prev) {
//            super(prev.getContext(),prev.getSourceSection());
//        }

        @Specialization
        SignalRuntime map(VirtualFrame frame, Object[] dependsOn,RubyProc block){
            return (SignalRuntime) callInit.call(frame,newSignal(),"initialize",block,dependsOn);
        }

        @CompilerDirectives.TruffleBoundary
        private SignalRuntime newSignal(){
            return (SignalRuntime) (new SignalRuntime.SignalRuntimeAllocator()).allocate(getContext(),getContext().getCoreLibrary().getBehaviorSimpleclass(),null);
            //return new SignalRuntime(getContext().getCoreLibrary().getBehaviorClass(),getContext());
        }

    }

    @CoreMethod(names = "source", isModuleFunction = true, required = 1)
    public abstract static class SourceNode extends CoreMethodNode{
        @Child
        CallDispatchHeadNode callInit;

        public SourceNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            callInit = DispatchHeadNodeFactory.createMethodCall(context, true);
        }

//        public SourceNode(SourceNode prev) {
//            super(prev);
//        }

        @Specialization SignalRuntime source(VirtualFrame frame, int value){
            final SignalRuntime self = newSignal();
            return (SignalRuntime) callInit.call(frame,newSignal(),"initialize",null,value);
        }

        @Specialization SignalRuntime source(VirtualFrame frame, Object value){
            final SignalRuntime self = newSignal();
            return (SignalRuntime) callInit.call(frame,newSignal(),"initialize",null,value);
        }

        @CompilerDirectives.TruffleBoundary
        private SignalRuntime newSignal(){
            return (SignalRuntime) (new SignalRuntime.SignalRuntimeAllocator()).allocate(getContext(),getContext().getCoreLibrary().getBehaviorSourceClass(),null);
        }

    }

}
