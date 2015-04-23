package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 22.04.15.
 */
public abstract class HandleOnChange extends Node {

    @Child
    private YieldDispatchHeadNode dispatchNode;
    @Child
    private ReadHeadObjectFieldNode readValue;


    HandleOnChange(RubyContext context){
        dispatchNode = new YieldDispatchHeadNode(context);
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public abstract Object execute(VirtualFrame frame, BehaviorObject obj);

    @Specialization(guards = "isEmpty(obj)")
    BehaviorObject onChange(VirtualFrame frame, BehaviorObject obj){
        return obj;
    }


    @Specialization(guards = "oneBlockStored(obj)")
    BehaviorObject onChangeOneBlockStored(VirtualFrame frame, BehaviorObject obj){
        RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
        dispatchNode.dispatch(frame,tmp[0],readValue.execute(obj));
        return obj;
    }

    @Specialization
    @ExplodeLoop
    BehaviorObject onChangeArrayStore(VirtualFrame frame, BehaviorObject obj){
        RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
        for(int i = 0; i < tmp.length;i++)
            dispatchNode.dispatch(frame,tmp[i],readValue.execute(obj));
        return obj;
    }

    boolean isEmpty(BehaviorObject obj){
        return obj.getFunctionStoreSize() == 0;
    }
    boolean oneBlockStored(BehaviorObject obj){
        return  obj.getFunctionStoreSize() == 1;
    }

}
