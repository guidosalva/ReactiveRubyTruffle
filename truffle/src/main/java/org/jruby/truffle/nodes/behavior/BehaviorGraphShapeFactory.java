package org.jruby.truffle.nodes.behavior;

/**
* Created by me on 01.04.15.
*/
public class BehaviorGraphShapeFactory {
    public static BehaviorGraphShape newEmptyShape(){
        return new GraphShapeTop();
    }
}
