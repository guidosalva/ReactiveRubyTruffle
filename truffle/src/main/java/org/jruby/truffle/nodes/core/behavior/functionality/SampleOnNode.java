package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.BehaviorObject;
import org.jruby.truffle.runtime.core.RubyProc;

/**
 * Created by me on 13.05.15.
 */
public class SampleOnNode extends Functionality{
    @Child
    private WriteValue writeValue;
    @Child
    private ReadHeadObjectFieldNode readId;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private ReadHeadObjectFieldNode readDepBev;


    private YieldDispatchHeadNode dispatchNode;
    SampleOnNode(RubyContext context) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readId = new ReadHeadObjectFieldNode(BehaviorOption.SAMPLE_ON_IDX);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readDepBev= new ReadHeadObjectFieldNode(BehaviorOption.SAMPLE_ON_DEP_BEV);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode,long sourceID) {
        long id = readId(self);
        if(lastNode.getId() == id){
            return writeValue.execute(self, readValueLastNode.execute(readDepBehavior(self)));
        }
        return false;
    }

    private long readId(BehaviorObject self){
        try{
            return readId.executeLong(self);
        }catch (UnexpectedResultException e){
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new RuntimeException("the type of the id of a behavior is a long value");
        }
    }

    private BehaviorObject readDepBehavior(BehaviorObject self){
        return (BehaviorObject) readDepBev.execute(self);
    }
}
