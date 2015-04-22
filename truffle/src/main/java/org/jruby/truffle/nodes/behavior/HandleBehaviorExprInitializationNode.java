package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

public class HandleBehaviorExprInitializationNode extends Node {

    @Child
    private ReadHeadObjectFieldNode readSigExpr;
    @Child
    private WriteHeadObjectFieldNode curValue;
    @Child
    private ReadHeadObjectFieldNode readValue;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;

    private final Object args = new Object[0];

    public HandleBehaviorExprInitializationNode(RubyContext context, SourceSection sourceSection) {
        dispatchNode = new YieldDispatchHeadNode(context);
        curValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readSigExpr = new ReadHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public static HandleBehaviorExprNode createHandleBehaviorExprNode(RubyContext context, SourceSection section) {
        return new HandleBehaviorExprNode(context, section);
    }


    public void execute(VirtualFrame frame, BehaviorObject self, Object[] dependsOn) {
        final RubyProc proc = getExpr(self);
        if (self.isFold()) {
            Object args[] = new Object[2];
            args[0] = readValue.execute(self);
            args[1] = readValueLastNode.execute((RubyBasicObject) dependsOn[0]);
            curValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
        } else {
            curValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
        }
    }


    private RubyProc getExpr(BehaviorObject self) {
        return (RubyProc) readSigExpr.execute(self);
    }

}