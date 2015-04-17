package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.management.*;
import org.jruby.runtime.Visibility;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;
import sun.misc.Signal;

import java.util.*;

/**
 * Created by me on 26.02.15.
 */

/*
    Some part of the Behavior class are implemented in behavior.rb
 */
@CoreClass(name = "BehaviorSimple")
public abstract class BehaviorSimpleNode extends BehaviorSuper {

    @CoreMethod(names = "initialize", needsBlock = true, argumentsAsArray = true)
    public abstract static class InitializeNode extends CoreMethodNode {
        @Child
        private WriteHeadObjectFieldNode writeSignalExpr;
        @Child ExecSignalExprNode execSignalExpr;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode("@sigExpr");
            execSignalExpr = new ExecSignalExprNode(context,sourceSection);
        }


        @Specialization
        public SignalRuntime init(VirtualFrame frame, SignalRuntime self, Object[] dependsOn, RubyProc signalExp) {
            if (dependsOn[0] instanceof SignalRuntime) {
                self.setupPropagationDep(dependsOn);
                writeSignalExpr.execute(self, signalExp);
                execSignalExpr.execSigExpr(frame,self);
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new RuntimeException("args need to be object array of signals");
            }
            return self;
        }
    }



    public static class ExecSignalExprNode extends Node {

        @Child
        private ReadInstanceVariableNode readSigExpr;
        @Child
        private WriteHeadObjectFieldNode value;

        @Child
        private YieldDispatchHeadNode dispatchNode;

        private final Object args = new Object[0];

        public ExecSignalExprNode(RubyContext context, SourceSection sourceSection) {
            dispatchNode = new YieldDispatchHeadNode(context);
            value = new WriteHeadObjectFieldNode(VALUE_VAR);
            readSigExpr = new ReadInstanceVariableNode(context, sourceSection, "@sigExpr", new SelfNode(context, sourceSection), false);
        }


        public Object execSigExpr(VirtualFrame frame, SignalRuntime self) {
            try {
                final RubyProc proc = readSigExpr.executeRubyProc(frame);
                value.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
            } catch (UnexpectedResultException e) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                e.printStackTrace();
                throw new RuntimeException();
            }
            return self;
        }


    }


    @CoreMethod(names = "propagation", required = 1, visibility = Visibility.PRIVATE)
    public abstract static class PropagationMethodNode extends CoreMethodNode {
        @Child
        BehaviorPropagationHeadNode propNode;

        public PropagationMethodNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            propNode = new BehaviorPropagationHeadNode(context, sourceSection);
        }

        @Specialization
        Object update(VirtualFrame frame, SignalRuntime self, long source) {
            propNode.handlePropagation(frame, self, source);
            return self;
        }
    }



    @CoreMethod(names = "value")
    public abstract static class ValueNode extends CoreMethodNode {

        @Child
        ReadInstanceVariableNode readValue;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
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
            readValue = new ReadInstanceVariableNode(context, sourceSection, VALUE_VAR, new SelfNode(context, sourceSection), false);
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
