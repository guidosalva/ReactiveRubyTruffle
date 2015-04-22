package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;


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


    public void execute(VirtualFrame frame, BehaviorObject self, long sourceId,BehaviorObject lastNode) {
        if(propagationNode.shouldContinuePropagation(frame, self, sourceId,lastNode)) {
            handleBehaviorExpr.execute(frame,self,lastNode);
            handlePropagation.execute(frame,self,sourceId);
        }
    }
}







