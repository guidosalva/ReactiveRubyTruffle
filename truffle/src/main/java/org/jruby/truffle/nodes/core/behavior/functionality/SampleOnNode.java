package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.BehaviorObject;

/**
 * Created by me on 13.05.15.
 */
public class SampleOnNode extends Functionality {
    @Child
    private WriteValue writeValue;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private ReadHeadObjectFieldNode sampleOnchange;

    @Child
    private ReadHeadObjectFieldNode readToReadDepBev;


    private YieldDispatchHeadNode dispatchNode;

    SampleOnNode(RubyContext context) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        sampleOnchange = new ReadHeadObjectFieldNode(BehaviorOption.SAMPLE_ON_CHANGE);
        readToReadDepBev= new ReadHeadObjectFieldNode(BehaviorOption.SAMPLE_TO_READ_DEP_BEV);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode, long sourceID) {
        BehaviorObject depObj = readSampleOnChange(self);
        if (depObj.isChanged())
            return writeValue.execute(self, readValueLastNode.execute(readToReadDepBehavior(self)));
        return false;
    }

    private BehaviorObject readSampleOnChange(BehaviorObject self) {
        return (BehaviorObject) sampleOnchange.execute(self);
    }

    private BehaviorObject readToReadDepBehavior(BehaviorObject self) {
        return (BehaviorObject) readToReadDepBev.execute(self);
    }
}
