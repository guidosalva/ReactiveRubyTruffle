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
    @Child
    ReadHeadObjectFieldNode readDepndsOn;



    public MergeNode(RubyContext context) {
        super(context);
        writeValue = new WriteValue();
        readValue = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        readDepndsOn = new ReadHeadObjectFieldNode(BehaviorOption.DEPENDS_ON);
    }

    //TODO quick fix for merge but needs to be implemented more efficant.
    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode,long sourceID) {
        if(self.isChain() || self.getSourceToSelfPathCount()[self.getIdxOfSource(sourceID)][1]== 0){
            return writeValue.execute(self, readValue.execute(lastNode));
        }else{
            BehaviorObject[] depsOn = (BehaviorObject[]) readDepndsOn.execute(self);
            int leftMost = -1;
            for(int i = 0 ; i < depsOn.length; i++){
                final BehaviorObject bev = depsOn[i];
                if(bev.getIdxOfSource(sourceID) >= 0 && leftMost == -1){
                    leftMost = i;
                }
            }
            return writeValue.execute(self, readValue.execute(depsOn[leftMost]));
        }

    }
}
