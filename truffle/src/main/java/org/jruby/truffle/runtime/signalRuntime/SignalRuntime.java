package org.jruby.truffle.runtime.signalRuntime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyClass;
import org.jruby.util.collections.WeakHashSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by me on 25.02.15.
 */
public class SignalRuntime extends RubyBasicObject {

    private static long max_id = 0;

    private SignalRuntime[] signalsThatDependOnSelf = new SignalRuntime[0];
    private final long id;
    @CompilerDirectives.CompilationFinal long[][] sourceToSelfPathCount;
    private  int count = 0;


    public SignalRuntime(RubyClass rubyClass, RubyContext context) {
        super(rubyClass);
        max_id += 1;
        id = max_id;
    }

    @CompilerDirectives.TruffleBoundary
    public void setupPropagationDep(SignalRuntime[] dependsOn) {
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
                //we ahve a source
                source.put(dependsOn[i].getId(),(long)1);
            }
        }
        final long[][] newSourceToSelfPathCount = new long[source.size()][];
        int idx = 0;
        ArrayList<Long> keys = new ArrayList<>(source.keySet());
        java.util.Collections.sort(keys);
        for (long key : keys) {
            newSourceToSelfPathCount[idx] = new long[2];
            newSourceToSelfPathCount[idx][0] = key;
            newSourceToSelfPathCount[idx][1] = source.get(key);
            idx += 1;
        }
        this.setSourceToSelfPathCount(newSourceToSelfPathCount);
    }
    @CompilerDirectives.TruffleBoundary
    public void setupPropagationDep(Object[] dependsOn) {
        final SignalRuntime[] tmp = new SignalRuntime[dependsOn.length];
        for (int i = 0; i < dependsOn.length; i++) {
            tmp[i] = (SignalRuntime) dependsOn[i];
        }
        setupPropagationDep(tmp);
    }


    @CompilerDirectives.TruffleBoundary
    public void addSignalThatDependsOnSelf(SignalRuntime obj) {
        if(signalsThatDependOnSelf == null){
            signalsThatDependOnSelf = new SignalRuntime[1];
            signalsThatDependOnSelf[0] = obj;
        }else {
            //check if we have it stored
            for(int i = 0; i < signalsThatDependOnSelf.length; i++){
                if(signalsThatDependOnSelf[i] ==obj)
                    return;
            }
            SignalRuntime[] newStore = new SignalRuntime[signalsThatDependOnSelf.length+1];
            System.arraycopy(signalsThatDependOnSelf,0,newStore,0,signalsThatDependOnSelf.length);
            newStore[signalsThatDependOnSelf.length] = obj;
            signalsThatDependOnSelf = newStore;
        }
    }

    public SignalRuntime[] getSignalsThatDependOnSelf(){
        return signalsThatDependOnSelf;
    }

    public void setSignalsThatDependOnSelf(SignalRuntime[] signalsThatDependOnSelf) {
        this.signalsThatDependOnSelf = signalsThatDependOnSelf;
    }


    public static class SignalRuntimeAllocator implements Allocator {

        @Override
        public RubyBasicObject allocate(RubyContext context, RubyClass rubyClass, Node currentNode) {
            return new SignalRuntime(rubyClass, rubyClass.getContext());
        }
    }


    public long getId() {
        return id;
    }

    public long[][] getSourceToSelfPathCount() {
        return sourceToSelfPathCount;
    }

    public void setSourceToSelfPathCount(long[][] sourceToSelfPathCount) {
        this.sourceToSelfPathCount = sourceToSelfPathCount;
    }

    @CompilerDirectives.TruffleBoundary
    public int getIdxOfSource(long id){
        for(int i = 0 ; i < sourceToSelfPathCount.length; i++){
            if( id == sourceToSelfPathCount[i][0])
                return i;
        }
        throw new RuntimeException("source id not found");
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

