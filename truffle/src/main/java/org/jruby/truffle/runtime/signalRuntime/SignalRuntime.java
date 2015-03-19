package org.jruby.truffle.runtime.signalRuntime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyClass;
import org.jruby.util.collections.WeakHashSet;

import java.lang.ref.WeakReference;

/**
 * Created by me on 25.02.15.
 */
public class SignalRuntime extends RubyBasicObject {

    private SignalRuntime[] signalsThatDependOnSelf = new SignalRuntime[0];

    private Object sourceInfo = new Object();


    private static long sigPropId = 0;
    private long curSigPropId = -1;
    private int curSigPropCount = 0;
    private int numSourceChanges = 0;

    public SignalRuntime(RubyClass rubyClass, RubyContext context) {
        super(rubyClass, context);
    }



//    //TODO add this code in the ast nodes
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

    public Object getSourceInfo() {
        return sourceInfo;
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

    public static long getSigPropId() {
        return sigPropId;
    }

    public static void setSigPropId(long sigPropId) {
        SignalRuntime.sigPropId = sigPropId;
    }

    public long getCurSigPropId() {
        return curSigPropId;
    }

    public void setCurSigPropId(long curSigPropId) {
        this.curSigPropId = curSigPropId;
    }

    public int getCurSigPropCount() {
        return curSigPropCount;
    }

    public void setCurSigPropCount(int curSigPropCount) {
        this.curSigPropCount = curSigPropCount;
    }

    public int getNumSourceChanges() {
        return numSourceChanges;
    }

    public void setNumSourceChanges(int numSourceChanges) {
        this.numSourceChanges = numSourceChanges;
    }


}

