package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.core.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class FilterNode extends Functionality {
    @Child
    private ReadHeadObjectFieldNode readFilterFunction;
    @Child
    private YieldDispatchHeadNode dispatchNode;
    @Child
    private WriteValue writeValue;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;



    public FilterNode(RubyContext context) {
        super(context);
        readFilterFunction = new ReadHeadObjectFieldNode(BehaviorOption.FILTER);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode,long sourceID){
        RubyProc proc = (RubyProc) readFilterFunction.execute(self);
        Object[] args = new Object[1];
        args[0] = readValueLastNode.execute(lastNode);
        Object filterRes = dispatchNode.dispatch(frame,proc,args);
        if(((Boolean) filterRes).booleanValue() == true){
            return writeValue.execute(self,readValueLastNode.execute(lastNode));
        }
        return false;
    }

}
