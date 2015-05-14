package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 14.05.15.
 */
public class MapNNode extends Functionality {

    MapNNode(RubyContext context) {
        super(context);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        return false;
    }
}
