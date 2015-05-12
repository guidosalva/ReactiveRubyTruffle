package org.jruby.truffle.nodes.core.behavior.utility;

import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
* Created by me on 17.04.15.
*/
public class BehaviorOption {
    public static final String VALUE_VAR = "@value";
//    public static final String FOLD_VALUE = "@foldValue";
//    public static final String FOLD_FUNCTION = "@foldFun";
    public static final String SIGNAL_EXPR = "@sigExpr";
    public static final String FILTER = "@filter";
    public static final String[] METHODS_TO_COPY = {"fold","foldN","onChange","remove"};

    private static final int size  = 3;
    private static final int idxSourceId = 0;
    private static final int idxLastNode = 1;
    private static final int idxChanged = 2;

    public static Object[] createBehaviorPropagationArgs(long sourceId, BehaviorObject lastNode,boolean changed){
        final Object[] args = new Object[size];
        args[idxSourceId] = sourceId;
        args[idxLastNode] = lastNode;
        args[idxChanged] = changed;
        return args;
    }
    public static long sourceId(Object[] args){
        return (long) args[idxSourceId];
    }
    public static BehaviorObject lastNode(Object[] args){
        return (BehaviorObject) args[idxLastNode];
    }
    public static boolean changed(Object[] args){
        return (boolean) args[idxChanged];
    }
}
