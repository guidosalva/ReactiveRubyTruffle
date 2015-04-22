package org.jruby.truffle.nodes.behavior;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.RubyArguments;
import org.jruby.truffle.runtime.core.RubyProc;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

import java.util.HashMap;

/**
 * Created by me on 22.04.15.
 */
public class DependencyStaticScope extends Node{



    public SignalRuntime[] execute(VirtualFrame frame, RubyProc proc){

        HashMap<FrameSlot, Integer> frameSlots = proc.getSharedMethodInfo().getOuterFrameSlots();
        SignalRuntime[] res  = new SignalRuntime[frameSlots.size()];
        int idx = 0;
        for(FrameSlot s : frameSlots.keySet()){
            int level = frameSlots.get(s);
            MaterializedFrame outFrame = getDeclarationFrame(proc.getDeclarationFrame(), level);
            if(outFrame.isObject(s)){
                try {
                    Object obj = outFrame.getObject(s);
                    if(obj instanceof SignalRuntime){
                        res[idx] = (SignalRuntime) obj;
                        idx++;
                    }
                } catch (FrameSlotTypeException e) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    e.printStackTrace();
                }
            }

        }
        if(idx < res.length){
            SignalRuntime[] tmp = new SignalRuntime[idx];
            System.arraycopy(res,0,tmp,0,idx);
            res = tmp;
        }
        return res;
    }

    private MaterializedFrame getDeclarationFrame(MaterializedFrame frame, int level) {
        if(level == 1)
            return frame;
        return getDeclarationFrameN(frame, level - 1);
    }

    @ExplodeLoop
    private MaterializedFrame getDeclarationFrameN(MaterializedFrame frame, int level) {
        MaterializedFrame parentFrame = frame;

        for (int n = 0; n < level; n++) {
            parentFrame = RubyArguments.getDeclarationFrame(parentFrame.getArguments());
        }

        return parentFrame;
    }
}
