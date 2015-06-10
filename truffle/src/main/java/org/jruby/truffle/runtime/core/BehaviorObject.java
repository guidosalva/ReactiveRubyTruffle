package org.jruby.truffle.runtime.core;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* Created by me on 25.02.15.
*/
public final class BehaviorObject extends RubyBasicObject {
    //TODO i need snapshot!

    public static final int TYPE_SOURCE = 0;
    public static final int TYPE_FOLD = 1;
    public static final int TYPE_FILTER = 2;
    public static final int TYPE_NORMAL = 3;
    public static final int TYPE_MERGE = 4;
    public static final int TYPE_MAP = 5;
    public static final int TYPE_TAKE = 6;
    public static final int TYPE_SKIP = 7;
    public static final int TYPE_BUFFER = 8;
    public static final int TYPE_MAPN = 9;

    private BehaviorObject[] signalsThatDependOnSelf = new BehaviorObject[0];


    //TODO that is only used for mapN, remove it for not mapN behaviours.
    //that is at at the moment compiler final
    //private WeakReference<BehaviorObject[]> selfDependsOn;
    private Object functionStore;
    private int functionStoreSize = 0;

    private final long id;

    //TODO move into dynamic object
    @CompilerDirectives.CompilationFinal long[][] sourceToSelfPathCount;
    @CompilerDirectives.CompilationFinal boolean chain;

    @CompilerDirectives.CompilationFinal int type = TYPE_NORMAL;


    //TODO should be moved into the OSM
    //they are not used by all behavior objects
    private int count = 0;
    private boolean changed = false;

    //todo add type here
    @CompilerDirectives.TruffleBoundary
    public BehaviorObject(RubyClass rubyClass, RubyContext context) {
        super(rubyClass);
        id = context.getEmptyBehaviorGraphShape().getNewId();
    }
    @CompilerDirectives.TruffleBoundary
    public BehaviorObject(int type, RubyContext context){
        super(context.getCoreLibrary().getBehaviorClass());
        this.type = type;
        id = context.getEmptyBehaviorGraphShape().getNewId();
    }

    //TODO i should be moved into an ast node
    //TODO i should be implemented more cleanly
    @CompilerDirectives.TruffleBoundary
    public void setupPropagationDep(BehaviorObject[] dependsOn) {
        //selfDependsOn = new WeakReference<BehaviorObject[]>(dependsOn);
        for (int i = 0; i < dependsOn.length; i++) {
            dependsOn[i].addSignalThatDependsOnSelf(this);
        }
        Map<Long, Long> source = new HashMap<>();
        for (int i = 0; i < dependsOn.length; i++) {
            final long[][] sourceToSelfPathCount = dependsOn[i].getSourceToSelfPathCount();
            if (sourceToSelfPathCount != null) {
                for (int j = 0; j < sourceToSelfPathCount.length; j++) {
                    long key = sourceToSelfPathCount[j][0];
                    long value = sourceToSelfPathCount[j][1];
                    if (source.containsKey(key)) {
                        source.put(key, source.get(key) + 1);
                    } else {
                        source.put(key, (long) 1);
                    }
                }
            }else{
                //we have a source
                if (source.containsKey(dependsOn[i].getId())) {
                    source.put(dependsOn[i].getId(), source.get(dependsOn[i].getId()) + 1);
                } else {
                    source.put(dependsOn[i].getId(), (long) 1);
                }
            }
        }
        final long[][] newSourceToSelfPathCount = new long[source.size()][];
        int idx = 0;
        ArrayList<Long> keys = new ArrayList<>(source.keySet());
        java.util.Collections.sort(keys);
        for (long key : keys) {
            newSourceToSelfPathCount[idx] = new long[2];
            newSourceToSelfPathCount[idx][0] = key;
            newSourceToSelfPathCount[idx][1] = source.get(key) -1;
            idx += 1;
        }
        chain = newSourceToSelfPathCount.length == 1 && newSourceToSelfPathCount[0][1] == 0;
        this.setSourceToSelfPathCount(newSourceToSelfPathCount);
    }

    @CompilerDirectives.TruffleBoundary
    public void setupPropagationDep(Object[] dependsOn) {
        final BehaviorObject[] tmp = new BehaviorObject[dependsOn.length];
        for (int i = 0; i < dependsOn.length; i++) {
            tmp[i] = (BehaviorObject) dependsOn[i];
        }
        setupPropagationDep(tmp);
    }


    @CompilerDirectives.TruffleBoundary
    public void addSignalThatDependsOnSelf(BehaviorObject obj) {
        if(signalsThatDependOnSelf == null){
            signalsThatDependOnSelf = new BehaviorObject[1];
            signalsThatDependOnSelf[0] = obj;
        }else {
            //check if we have it stored
            for(int i = 0; i < signalsThatDependOnSelf.length; i++){
                if(signalsThatDependOnSelf[i] ==obj)
                    return;
            }
            BehaviorObject[] newStore = new BehaviorObject[signalsThatDependOnSelf.length+1];
            System.arraycopy(signalsThatDependOnSelf,0,newStore,0,signalsThatDependOnSelf.length);
            newStore[signalsThatDependOnSelf.length] = obj;
            signalsThatDependOnSelf = newStore;
        }
    }


    public BehaviorObject[] getSignalsThatDependOnSelf(){
        return signalsThatDependOnSelf;
    }

    public void setSignalsThatDependOnSelf(BehaviorObject[] signalsThatDependOnSelf) {
        this.signalsThatDependOnSelf = signalsThatDependOnSelf;
    }

    public int getType() {
        return type;
    }

    public boolean isMerge() {
        return type == TYPE_MERGE;
    }


    public static class SignalRuntimeAllocator implements Allocator {

        @Override
        @CompilerDirectives.TruffleBoundary
        public RubyBasicObject allocate(RubyContext context, RubyClass rubyClass, Node currentNode) {
            return new BehaviorObject(rubyClass, rubyClass.getContext());
        }
    }


    public long getId() {
        return id;
    }

    public long[][] getSourceToSelfPathCount() {
        return sourceToSelfPathCount;
    }

    public int sourceToSelfSize(){
        return sourceToSelfPathCount.length;
    }

    public void setSourceToSelfPathCount(long[][] sourceToSelfPathCount) {
        this.sourceToSelfPathCount = sourceToSelfPathCount;
    }


    public int getIdxOfSource(long id){
        for(int i = 0 ; i < sourceToSelfPathCount.length; i++){
            if( id == sourceToSelfPathCount[i][0])
                return i;
        }
        return -1;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFold() {
            this.type = TYPE_FOLD;
    }

    public boolean isChain() {
        return chain;
    }

    public boolean isFold() {
        return type == TYPE_FOLD;
    }



    public boolean isNormal(){
        return type == TYPE_NORMAL;
    }
    public boolean isFilter(){
        return type == TYPE_FILTER;
    }

    public Object getFunctionStore() {
        return functionStore;
    }

    public void setFunctionStore(Object functionStore) {
        this.functionStore = functionStore;
    }

    public int getFunctionStoreSize() {
        return functionStoreSize;
    }

    public void setFunctionStoreSize(int functionStoreSize) {
        this.functionStoreSize = functionStoreSize;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}

