package org.jruby.truffle.nodes.core.behavior;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
class InitFold extends Node {
    private final RubyContext context;

    InitFold(RubyContext context){
        this.context = context;
    }

    BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents) {
        BehaviorObject newSignal = new BehaviorObject(context.getCoreLibrary().getBehaviorClass(),context);
        newSignal.setupPropagationDep(parents);
        newSignal.setFold();
        return newSignal;
    }

}