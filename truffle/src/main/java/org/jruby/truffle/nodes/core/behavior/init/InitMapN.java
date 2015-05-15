package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 14.05.15.
 */
public class InitMapN extends Node{
    private final RubyContext context
            ;
    @Child
    private WriteHeadObjectFieldNode writeValue;
    @Child
    private WriteHeadObjectFieldNode writeMapExp;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;
    public InitMapN(RubyContext context) {
        this.context = context;
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        writeMapExp = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] behaviorObjects, RubyProc proc) {
            BehaviorObject newBe = new BehaviorObject(BehaviorObject.TYPE_MAPN, context);
            newBe.setupPropagationDep(behaviorObjects);
            writeMapExp.execute(newBe,proc);
            Object[] args = new Object[behaviorObjects.length];
            for(int i= 0; i < behaviorObjects.length; i++){
                args[i] = readValueLastNode.execute(behaviorObjects[i]);
            }
            writeValue.execute(newBe,dispatchNode.dispatch(frame,proc,args));
            return newBe;
    }
}
