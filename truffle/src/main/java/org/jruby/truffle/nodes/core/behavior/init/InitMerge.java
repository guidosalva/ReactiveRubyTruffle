package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 13.05.15.
 */
public class InitMerge extends Node {
    private final RubyContext context;

    @Child
    WriteHeadObjectFieldNode writeValue;
    @Child
    ReadHeadObjectFieldNode readValue;

    public InitMerge(RubyContext context) {
        this.context = context;
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }


    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] deps) {
        BehaviorObject newSignal = new BehaviorObject(BehaviorObject.TYPE_MERGE, context);
        newSignal.setupPropagationDep(deps);
        writeValue.execute(newSignal, readValue.execute(deps[0]));
        return newSignal;
    }
}