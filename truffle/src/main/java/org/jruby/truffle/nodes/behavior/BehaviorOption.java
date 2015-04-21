package org.jruby.truffle.nodes.behavior;

import org.jruby.truffle.runtime.signalRuntime.SignalRuntime;

/**
 * Created by me on 17.04.15.
 */
public class BehaviorOption {
    public static final String VALUE_VAR = "@value";
//    public static final String FOLD_VALUE = "@foldValue";
//    public static final String FOLD_FUNCTION = "@foldFun";
    public static final String SIGNAL_EXPR = "@sigExpr";


    private static final int size  = 2;
    private static final int idxSourceId = 0;
    private static final int idxLastNode = 1;

    public static Object[] createBehaviorPropagationArgs(long sourceId, SignalRuntime lastNode){
        final Object[] args = new Object[size];
        args[idxSourceId] = sourceId;
        args[idxLastNode] = lastNode;
        return args;
    }
    public static long sourceId(Object[] args){
        return (long) args[idxSourceId];
    }
    public static SignalRuntime lastNode(Object[] args){
        return (SignalRuntime) args[idxLastNode];
    }
}
