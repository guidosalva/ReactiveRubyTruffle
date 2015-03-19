package org.jruby.truffle.nodes.core;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

/**
 * Created by me on 19.03.15.
 */
//TODO not working and not implementation unfinished
public class BehaviorAddDependencyHeadNode extends Node {

    private final RubyContext context;

    @Child BehaviorAddDependencyNode addDep;

    public BehaviorAddDependencyHeadNode(RubyContext context,SourceSection section){
        super(section);
        this.context = context;
        addDep = new BehaviorAddDependencyUninitialized(section);
    }

    public void addDependency(VirtualFrame frame, SignalRuntime signal, SignalRuntime dependency){
        addDep.addDependency(frame,signal,dependency);
    }
}

abstract class BehaviorAddDependencyNode extends Node{
    BehaviorAddDependencyNode(SourceSection section){
        super(section);
    }

    abstract void addDependency(VirtualFrame frame, SignalRuntime signal, SignalRuntime dependency);
}

class BehaviorAddDependencyUninitialized extends BehaviorAddDependencyNode{

    BehaviorAddDependencyUninitialized(SourceSection section) {
        super(section);
    }

    public void addDependency(VirtualFrame frame,SignalRuntime signal, SignalRuntime dependency) {
        final SignalRuntime[] deps = signal.getSignalsThatDependOnSelf();
        SignalRuntime[] newDesp = new SignalRuntime[deps.length+1];
        System.arraycopy(deps,0,newDesp,0,newDesp.length);
        newDesp[deps.length] = dependency;
        signal.setSignalsThatDependOnSelf(newDesp);
    }
}
