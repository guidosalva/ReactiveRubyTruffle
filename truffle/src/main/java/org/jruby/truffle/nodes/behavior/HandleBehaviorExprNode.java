package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

public class HandleBehaviorExprNode extends Node {

    @Child
    private ReadInstanceVariableNode readSigExpr;
    @Child
    private WriteHeadObjectFieldNode curValue;
    @Child
    private ReadInstanceVariableNode readValue;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;

    private final Object args = new Object[0];

    public HandleBehaviorExprNode(RubyContext context, SourceSection sourceSection) {
        dispatchNode = new YieldDispatchHeadNode(context);
        curValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readSigExpr = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.SIGNAL_EXPR, new SelfNode(context, sourceSection), false);
        readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public static HandleBehaviorExprNode createHandleBehaviorExprNode(RubyContext context, SourceSection section) {
        return new HandleBehaviorExprNode(context, section);
    }


    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        final RubyProc proc = getExpr(frame);
        if (self.isFold()) {
            Object args[] = new Object[2];
            args[0] = readValue.execute(frame);
            args[1] = readValueLastNode.execute(lastNode);
            curValue.execute(self,dispatchNode.dispatchWithSignal(frame, proc, self, args));
        } else {
            curValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
        }
    }


    private RubyProc getExpr(VirtualFrame frame) {
        try {
            return readSigExpr.executeRubyProc(frame);
        } catch (UnexpectedResultException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}