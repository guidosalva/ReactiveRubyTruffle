package org.jruby.truffle.nodes.core.behavior.functionality;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.WriteValue;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.core.BehaviorObject;

/**
 * Created by me on 12.05.15.
 */
public class NormalBehavior extends Functionality{
    @Child
    private ReadInstanceVariableNode readSigExpr;
    @Child
    WriteValue writeValue;
    @Child
    private YieldDispatchHeadNode dispatchNode;

    private final Object args = new Object[0];

    public NormalBehavior(RubyContext context, SourceSection sourceSection) {
        super(context);
        dispatchNode = new YieldDispatchHeadNode(context);
        writeValue = new WriteValue();
        readSigExpr = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.SIGNAL_EXPR, new SelfNode(context, sourceSection), false);
    }

    public boolean execute(VirtualFrame frame, BehaviorObject self, BehaviorObject lastNode,long sourceID) {
        RubyBasicObject proc = getExpr(frame);
        return writeValue.execute(self, dispatchNode.dispatchWithSignal(frame, proc, self, args));
    }

    private RubyBasicObject getExpr(VirtualFrame frame) {
        try {
            return readSigExpr.executeRubyBasicObject(frame);
        } catch (UnexpectedResultException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
