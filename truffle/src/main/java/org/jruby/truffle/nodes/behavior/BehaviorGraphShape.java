package org.jruby.truffle.nodes.behavior;

/**
 *
 */
public interface BehaviorGraphShape {
    long getNewId();
}

class GraphShapeTop implements  BehaviorGraphShape{
    private long maxId = 0;

    @Override
    public long getNewId() {
        maxId += 1;
        return maxId;
    }

}
