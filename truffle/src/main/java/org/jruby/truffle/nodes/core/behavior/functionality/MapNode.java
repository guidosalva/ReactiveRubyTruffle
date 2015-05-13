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
 * Created by me on 13.05.15.
 */
public class MapNode extends Functionality{
    @Child
    private WriteValue writeValue;
    @Child
    private ReadHeadObjectFieldNode readMapExp;
    @Child
    private ReadHeadObjectFieldNode readValueLastNode;
    @Child


    private YieldDispatchHeadNode dispatchNode;
    MapNode(RubyContext context) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readMapExp = new ReadHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
        readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
    }

    @Override
    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode) {
        RubyProc proc = (RubyProc) readMapExp.execute(self);
        Object args[] = new Object[1];
        args[0] = readValueLastNode.execute(lastNode);
        return writeValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
    }
}
