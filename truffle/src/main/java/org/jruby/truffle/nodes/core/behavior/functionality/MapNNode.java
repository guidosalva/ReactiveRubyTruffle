package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 14.05.15.
 */
public class MapNNode extends Functionality {

    @Child
    private WriteValue writeValue;
    @Child
    private ReadHeadObjectFieldNode readMapExp;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child
    private YieldDispatchHeadNode dispatchNode;


    MapNNode(RubyContext context) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readMapExp = new ReadHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }


    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        RubyProc proc = (RubyProc) readMapExp.execute(self);
        Object[] args = new Object[self.getSelfDependsOn().length];
        for(int i= 0; i < self.getSelfDependsOn().length; i++){
            args[i] = readValueLastNode.execute(self.getSelfDependsOn()[i]);
        }
        return writeValue.execute(self,dispatchNode.dispatch(frame,proc,args));
    }
}
