package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class FilterNode extends Functionality {

    public FilterNode(RubyContext context) {
        super(context);
    }

    @Override
    void execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {

    }
}
