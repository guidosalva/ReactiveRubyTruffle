package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class FoldNode extends Functionality {

    @Child
    private ReadInstanceVariableNode readSigExpr;
    @Child
    private WriteHeadObjectFieldNode writeValue;
    @Child
    private ReadInstanceVariableNode readValue;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;


    public FoldNode(RubyContext context) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readSigExpr = new ReadInstanceVariableNode(context, null, BehaviorOption.SIGNAL_EXPR, new SelfNode(context, null), false);
        readValue = new ReadInstanceVariableNode(context, null, BehaviorOption.VALUE_VAR, new SelfNode(context, null), false);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }



    public void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        RubyProc proc = getExpr(frame);
        Object args[] = new Object[2];
        args[0] = readValue.execute(frame);
        args[1] = readValueLastNode.execute(lastNode);
        writeValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
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
