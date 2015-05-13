package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 13.05.15.
 */
public class
        MergeNode extends Functionality {
    @Child
    WriteValue writeValue;
    @Child
    ReadHeadObjectFieldNode readValue;

    public MergeNode(RubyContext context) {
        super(context);
        writeValue = new WriteValue();
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        return writeValue.execute(self, readValue.execute(lastNode));
    }
}
