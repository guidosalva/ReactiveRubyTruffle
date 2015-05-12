package org.jruby.truffle.nodes.core.behavior.utility;

import org.jruby.truffle.nodes.core.behavior.utility.BehaviorGraphShape;
import org.jruby.truffle.nodes.core.behavior.utility.GraphShapeTop;

/**
* Created by me on 01.04.15.
*/
public class BehaviorGraphShapeFactory {
    public static BehaviorGraphShape newEmptyShape(){
        return new GraphShapeTop();
    }
}
