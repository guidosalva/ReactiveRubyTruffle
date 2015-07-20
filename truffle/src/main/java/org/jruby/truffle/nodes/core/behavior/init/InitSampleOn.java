package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.BehaviorObject;

/**
 * Created by me on 01.07.15.
 */
public class InitSampleOn extends Node{

    private final RubyContext context;

    @Child
    private WriteHeadObjectFieldNode writeValue;

    @Child
    private WriteHeadObjectFieldNode writeReadFromDepBev;

    @Child
    private WriteHeadObjectFieldNode writeSampleOnChange;


    @Child
    private ReadHeadObjectFieldNode readValueLastNode;

    public InitSampleOn(RubyContext context) {
        this.context = context;
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        writeReadFromDepBev= new WriteHeadObjectFieldNode(BehaviorOption.SAMPLE_TO_READ_DEP_BEV);
        writeSampleOnChange  = new WriteHeadObjectFieldNode(BehaviorOption.SAMPLE_ON_CHANGE);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, BehaviorObject arg) {
        BehaviorObject newBe = new BehaviorObject(BehaviorObject.TYPE_SAMPLEON,context);
        newBe.setupPropagationDep(new BehaviorObject[]{self,arg});
        writeSampleOnChange.execute(newBe, arg);
        writeValue.execute(newBe, readValueLastNode.execute(self));
        writeReadFromDepBev.execute(newBe, self);
        return newBe;
    }

}
