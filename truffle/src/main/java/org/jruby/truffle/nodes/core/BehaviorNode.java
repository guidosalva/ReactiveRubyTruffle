package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.UndefinedPlaceholder;
import org.jruby.truffle.runtime.control.RaiseException;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import org.jruby.truffle.runtime.util.FileUtils;
import org.jruby.util.ByteList;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.rmi.UnexpectedException;

/**
 * Created by me on 26.02.15.
 */


@CoreClass(name = "Behavior")
public abstract class BehaviorNode {



    @CoreMethod(names="emit",required = 1)
    public abstract static class EmitNode extends CoreMethodNode{

        public EmitNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public EmitNode(EmitNode prev) {
            super(prev);
        }

        @Specialization
        int emit(SignalRuntime s,int value){
            System.out.println("Emit value " + value);
            return value;
        }

        @Specialization
        double emit(SignalRuntime s,double value){
            System.out.println("Emit value " + value);
            return value;
        }

        @Specialization
        Object emit(SignalRuntime s,Object value){
            System.out.println("Emit value " + value);
            return value;
        }
    }

    @CoreMethod(names="value")
    public abstract static class ValueNode extends CoreMethodNode{

        @Child ReadInstanceVariableNode readValue;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, "@value", new SelfNode( context,sourceSection),false);
        }

        public ValueNode(ValueNode prev) {
            super(prev);
            readValue = prev.readValue;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        int valueInt(VirtualFrame frame, SignalRuntime s) throws UnexpectedResultException{
            System.out.println("now int");
            int value = readValue.executeIntegerFixnum(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        double valueDouble(VirtualFrame frame, SignalRuntime s) throws UnexpectedResultException{
            System.out.println("now double");
            double value = readValue.executeFloat(frame);
            return value;
        }
        @Specialization
        Object valueObject(VirtualFrame frame, SignalRuntime s) {
            System.out.println("now object");
            Object value = readValue.execute(frame);
            return value;
        }
//        @Specialization
//        Object nowObject(RubyBasicObject s){
//            System.out.println("now object");
//            return 0;
//        }


        static boolean isInt(SignalRuntime s){
            return true;
        }
        static boolean isDouble(SignalRuntime s){
            return false;
        }
        static boolean isNotPrimitve(SignalRuntime s){
            return !isInt(s) && !isDouble(s);
        }



    }
    @CoreMethod(names = "initializeBev", optional = 1, needsBlock = true)
    public abstract static class InitializeNode extends CoreMethodNode {

        @Child private ModuleNodes.InitializeNode moduleInitializeNode;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public InitializeNode(InitializeNode prev) {
            super(prev);
        }

        void moduleInitialize(VirtualFrame frame, RubyClass rubyClass, RubyProc block) {
            if (moduleInitializeNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                moduleInitializeNode = insert(ModuleNodesFactory.InitializeNodeFactory.create(getContext(), getSourceSection(), new RubyNode[]{null,null}));
            }
            moduleInitializeNode.executeInitialize(frame, rubyClass, block);
        }

        @Specialization
        public RubyClass initialize(RubyClass rubyClass, UndefinedPlaceholder superclass, UndefinedPlaceholder block) {
            return initialize(rubyClass, getContext().getCoreLibrary().getObjectClass(), block);
        }

        @Specialization
        public RubyClass initialize(RubyClass rubyClass, RubyClass superclass, UndefinedPlaceholder block) {
            rubyClass.initialize(superclass);
            return rubyClass;
        }

        @Specialization
        public RubyClass initialize(VirtualFrame frame, RubyClass rubyClass, UndefinedPlaceholder superclass, RubyProc block) {
            return initialize(frame, rubyClass, getContext().getCoreLibrary().getObjectClass(), block);
        }

        @Specialization
        public RubyClass initialize(VirtualFrame frame, RubyClass rubyClass, RubyClass superclass, RubyProc block) {
            rubyClass.initialize(superclass);
            moduleInitialize(frame, rubyClass, block);
            return rubyClass;
        }

    }

//
//    @CoreMethod(names = "now")
//    public abstract static class NowNode extends CoreMethodNode {
//
//        public NowNode(RubyContext context, SourceSection sourceSection) {
//            super(context, sourceSection);
//        }
//    }
//
//    @CoreMethod(names = "emit", required = 1)
//    public abstract static class EmitNode extends CoreMethodNode {
//
//        public EmitNode(RubyContext context, SourceSection sourceSection) {
//            super(context, sourceSection);
//        }
//    }
//    @CoreMethod(names = "value")
//    public abstract static class ValueNode extends CoreMethodNode {
//
//        public ValueNode(RubyContext context, SourceSection sourceSection) {
//            super(context, sourceSection);
//        }
//    }
}
