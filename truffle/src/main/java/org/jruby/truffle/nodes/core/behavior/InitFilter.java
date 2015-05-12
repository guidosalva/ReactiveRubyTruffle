package org.jruby.truffle.nodes.core.behavior;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
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

    public InitFilter(RubyContext context, SourceSection sourceSection) {
        this.context = context;
        this.sourceSecion = sourceSection;
        writeFilter = new WriteHeadObjectFieldNode(BehaviorOption.FILTER);

    }

    public BehaviorObject execute(VirtualFrame frame, BehaviorObject self, RubyProc proc) {
        writeFilter.execute(self,proc);

        return null;
    }
}
