package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 14.05.15.
 */
public class InitSkip extends Node{


    private final RubyContext context;
    @Child WriteHeadObjectFieldNode writeTakeNum;
    @Child WriteHeadObjectFieldNode writeValue;
    @Child
    ReadHeadObjectFieldNode readSelfValue;

    public InitSkip(RubyContext context) {
        this.context = context;
        this.writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        this.writeTakeNum = new WriteHeadObjectFieldNode(BehaviorOption.TAKE_NUM);
        readSelfValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, int value) {
        BehaviorObject newBeh = new BehaviorObject(BehaviorObject.TYPE_SKIP,context);
        newBeh.setupPropagationDep(new BehaviorObject[]{self});
        writeValue.execute(newBeh,readSelfValue.execute(self));
        writeTakeNum.execute(newBeh,value -1);
        return newBeh;
    }
}
