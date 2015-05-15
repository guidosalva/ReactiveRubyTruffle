package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.Ruby;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class InitFold extends Node {
    private final RubyContext context;
    @Child
    WriteHeadObjectFieldNode writeFoldValue;
    @Child
    WriteHeadObjectFieldNode writeFoldFunction;
    @Child
    ReadHeadObjectFieldNode readValueLastNode;
    @Child
    YieldDispatchHeadNode dispatchNode;

    public InitFold(RubyContext context){
        this.context = context;
        writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        dispatchNode = new YieldDispatchHeadNode(context);
    }



    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents, int init, RubyProc proc) {
        BehaviorObject newSignal = createNew(parents, proc);
        Object args[] = new Object[2];
        args[0] = init;
        args[1] = readValueLastNode.execute(parents[0]);
        writeFoldValue.execute(newSignal,dispatchNode.dispatch(frame, proc, args));
        return newSignal;
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents, long init, RubyProc proc) {
        BehaviorObject newSignal = createNew(parents, proc);
        Object args[] = new Object[2];
        args[0] = init;
        args[1] = readValueLastNode.execute(parents[0]);
        writeFoldValue.execute(newSignal,dispatchNode.dispatch(frame, proc, args));
        return newSignal;
    }
    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents, double init, RubyProc proc) {
        BehaviorObject newSignal = createNew(parents, proc);
        Object args[] = new Object[2];
        args[0] = init;
        args[1] = readValueLastNode.execute(parents[0]);
        writeFoldValue.execute(newSignal,dispatchNode.dispatch(frame, proc, args));
        return newSignal;
    }
    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents, Object init, RubyProc proc) {
        BehaviorObject newSignal = createNew(parents, proc);
        Object args[] = new Object[2];
        args[0] = init;
        args[1] = readValueLastNode.execute(parents[0]);
        writeFoldValue.execute(newSignal,dispatchNode.dispatch(frame, proc, args));
        return newSignal;
    }

    private BehaviorObject createNew(BehaviorObject[] parents, RubyProc proc){
        BehaviorObject newSignal = new BehaviorObject(context.getCoreLibrary().getBehaviorClass(),context);
        newSignal.setupPropagationDep(parents);
        newSignal.setFold();
        writeFoldFunction.execute(newSignal,proc);
        return newSignal;
    }
}