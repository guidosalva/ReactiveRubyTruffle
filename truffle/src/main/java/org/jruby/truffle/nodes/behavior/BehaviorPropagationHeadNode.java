package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.dispatch.CallDispatchHeadNode;
import org.jruby.truffle.nodes.dispatch.DispatchHeadNodeFactory;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;


public class BehaviorPropagationHeadNode extends Node {
    @Child HandlePropagation handlePropagation;
    @Child
    ShouldContinuePropagationNode propagationNode;
    @Child HandleBehaviorExprNode handleBehaviorExpr;

    public BehaviorPropagationHeadNode(RubyContext context, SourceSection section) {
        super(section);
        propagationNode = ShouldContinuePropagationNode.createUninitializedShouldPropagationNode(context,section);
        handleBehaviorExpr = HandleBehaviorExprNode.createHandleBehaviorExprNode(context,section);
        handlePropagation = new HandlePropagation(context,section);
    }


    public void execute(VirtualFrame frame, SignalRuntime self, long sourceId,SignalRuntime lastNode) {
        if(propagationNode.shouldContinuePropagation(frame, self, sourceId,lastNode)) {
            handleBehaviorExpr.execute(frame,self,lastNode);
            handlePropagation.execute(frame,self,sourceId);
        }
    }
}







