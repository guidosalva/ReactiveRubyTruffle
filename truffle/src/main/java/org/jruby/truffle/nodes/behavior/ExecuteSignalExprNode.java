package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

public class ExecuteSignalExprNode extends Node {

    @Child
    private ReadInstanceVariableNode readSigExpr;
    @Child
    private WriteHeadObjectFieldNode value;

    @Child
    private YieldDispatchHeadNode dispatchNode;

    private final Object args = new Object[0];

    public ExecuteSignalExprNode(RubyContext context, SourceSection sourceSection) {
        dispatchNode = new YieldDispatchHeadNode(context);
        value = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
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