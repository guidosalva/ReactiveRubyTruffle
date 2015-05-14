package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 14.05.15.
 */
public class InitMapN extends Node{
    private final RubyContext context
            ;

    public InitMapN(RubyContext context) {
        this.context = context;
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject[] behaviorObjects, RubyProc proc) {
            return null;
    }
}
