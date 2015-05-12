package org.jruby.truffle.nodes.core.behavior.utility;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.jruby.Ruby;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */

//TODO check if this is the way to go!!!!
    //TODO it looks wired
    //TODO i may need to make specialized nodes here
public class WriteValue extends Node {
    @Child
    WriteHeadObjectFieldNode writeValue;
    @Child
    ReadHeadObjectFieldNode readValue;


    public WriteValue(){
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    public boolean execute(BehaviorObject self, int value){
        try{
            return  executeInt(self,value,readValue.executeInteger(self));
        }catch (UnexpectedResultException e){
            return executeInt(self,value,readValue.execute(self));
        }
    }
    public boolean executeInt(BehaviorObject self,int value, int oldValue){
        writeValue.execute(self,value);
        return value != oldValue;
    }
    public boolean executeInt(BehaviorObject self,int value, Object oldValue){
        writeValue.execute(self,value);
        return true;
    }

    public boolean execute(BehaviorObject self, long value){
        try{
            return  executeLong(self, value, readValue.executeLong(self));
        }catch (UnexpectedResultException e){
            return executeLong(self, value, readValue.execute(self));
        }
    }
    public boolean executeLong(BehaviorObject self,long value, long oldValue){
        writeValue.execute(self,value);
        return value != oldValue;
    }
    public boolean executeLong(BehaviorObject self,long value, Object oldValue){
        writeValue.execute(self,value);
        return true;
    }

    public boolean execute(BehaviorObject self, double value){
        try{
            return  executeDouble(self, value, readValue.executeDouble(self));
        }catch (UnexpectedResultException e){
            return executeDouble(self, value, readValue.execute(self));
        }
    }
    public boolean executeDouble(BehaviorObject self,double value, double oldValue){
        writeValue.execute(self,value);
        return value != oldValue;
    }
    public boolean executeDouble(BehaviorObject self,double value, Object oldValue){
        writeValue.execute(self,value);
        return true;
    }


    public boolean execute(BehaviorObject self, Object value){
       return  executeObject(self,value,readValue.execute(self));
    }
    public boolean executeObject(BehaviorObject self,Object value, Object oldValue){
        writeValue.execute(self,value);
        return value != oldValue;
    }
}
