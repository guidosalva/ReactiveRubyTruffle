package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class InitFold extends Node {
    private final RubyContext context;

    public InitFold(RubyContext context){
        this.context = context;
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] parents) {
        BehaviorObject newSignal = new BehaviorObject(context.getCoreLibrary().getBehaviorClass(),context);
        newSignal.setupPropagationDep(parents);
        newSignal.setFold();
        return newSignal;
    }

}