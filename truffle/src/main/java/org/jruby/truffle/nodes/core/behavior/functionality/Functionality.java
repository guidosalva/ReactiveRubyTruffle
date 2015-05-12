package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
abstract class Functionality extends Node
{
    protected final RubyContext context;

    Functionality(RubyContext context){
        this.context = context;
    }
    abstract void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode);
}
