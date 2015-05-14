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
 * Created by me on 13.05.15.
 */
public class InitMap extends Node{
    private final RubyContext context;

    @Child
    private WriteHeadObjectFieldNode writeValue;
    @Child
    private WriteHeadObjectFieldNode writeMapExp;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;

    public InitMap(RubyContext context) {
        this.context = context;
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        writeMapExp = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, RubyProc proc) {
        BehaviorObject newBe = new BehaviorObject(BehaviorObject.TYPE_MAP,context);
        newBe.setupPropagationDep(new BehaviorObject[]{self});
        writeMapExp.execute(newBe,proc);
        Object args[] = new Object[1];
        args[0] = readValueLastNode.execute(self);
        writeValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
        return newBe;
    }

}
