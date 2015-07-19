package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.core.behavior.functionality.HandleBehaviorFunctionality;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.core.BehaviorObject;


public class InitBehaviorExpression extends Node {

    @Child
    private ReadHeadObjectFieldNode readSigExpr;

    @Child
    private WriteHeadObjectFieldNode curValue;
    @Child
    private YieldDispatchHeadNode dispatchNode;

    private final Object args = new Object[0];

    public InitBehaviorExpression(RubyContext context, SourceSection sourceSection) {
        dispatchNode = new YieldDispatchHeadNode(context);
        curValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readSigExpr = new ReadHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
    }



    public void execute(VirtualFrame frame, BehaviorObject self, Object[] dependsOn) {
        final RubyBasicObject proc = getExpr(self);
        curValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
    }


    private RubyBasicObject getExpr(BehaviorObject self) {
        return (RubyBasicObject) readSigExpr.execute(self);
    }

}