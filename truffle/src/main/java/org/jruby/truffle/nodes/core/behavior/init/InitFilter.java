package org.jruby.truffle.nodes.core.behavior.init;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.interop.messages.Write;
import org.jruby.truffle.nodes.core.behavior.functionality.FilterNode;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class InitFilter extends Node {
    private final SourceSection sourceSecion;
    private final RubyContext context;

    @Child
    WriteHeadObjectFieldNode writeFilter;
    @Child
    WriteHeadObjectFieldNode writeValue;
    @Child
    FilterNode filterNode;

    public InitFilter(RubyContext context, SourceSection sourceSection) {
        this.context = context;
        this.sourceSecion = sourceSection;
        writeFilter = new WriteHeadObjectFieldNode(BehaviorOption.FILTER);
        writeValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
        filterNode = new FilterNode(context);
    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, int initValue, RubyProc proc) {
        BehaviorObject newBehavior = new BehaviorObject(BehaviorObject.TYPE_FILTER,context);
        writeFilter.execute(newBehavior,proc);
        writeValue.execute(newBehavior,initValue);
        filterNode.execute(frame,newBehavior,self);
        return newBehavior;
    }
    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, long initValue, RubyProc proc) {
        BehaviorObject newBehavior = new BehaviorObject(BehaviorObject.TYPE_FILTER,context);
        writeFilter.execute(newBehavior,proc);
        writeValue.execute(newBehavior,initValue);
        filterNode.execute(frame,newBehavior,self);
        return newBehavior;
    }
    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, double initValue, RubyProc proc) {
        BehaviorObject newBehavior = new BehaviorObject(BehaviorObject.TYPE_FILTER,context);
        writeFilter.execute(newBehavior,proc);
        writeValue.execute(newBehavior,initValue);
        filterNode.execute(frame,newBehavior,self);
        return newBehavior;
    }
    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, Object initValue, RubyProc proc) {
        BehaviorObject newBehavior = new BehaviorObject(BehaviorObject.TYPE_FILTER,context);
        writeFilter.execute(newBehavior,proc);
        writeValue.execute(newBehavior,initValue);
        filterNode.execute(frame,newBehavior,self);
        return newBehavior;
    }
}
