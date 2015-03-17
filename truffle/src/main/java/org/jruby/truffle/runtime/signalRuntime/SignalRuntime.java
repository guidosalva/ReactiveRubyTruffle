package org.jruby.truffle.runtime.signalRuntime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectFactory;
import com.oracle.truffle.api.object.Layout;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyClass;

/**
 * Created by me on 25.02.15.
 */
public class SignalRuntime extends RubyBasicObject {

    private SignalRuntime[] signalsThatDependOnSelf = new SignalRuntime[0];



    private SignalRuntime[] selfDependsOn;

    private Object sourceInfo = new Object();

    private static long sigPropId = 0;
    private long curSigPropId = -1;
    private int curSigPropCount = 0;
    private int numSourceChanges = 0;

    public SignalRuntime(RubyClass rubyClass, RubyContext context) {
        super(rubyClass, context);
        selfDependsOn = new SignalRuntime[0];
    }



    //TODO add this code in the ast nodes
    //that should aneble furter optimitzation
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


    public static class SignalRuntimeAllocator implements Allocator {

        @Override
        //@CompilerDirectives.TruffleBoundary
        public RubyBasicObject allocate(RubyContext context, RubyClass rubyClass, Node currentNode) {
            return new SignalRuntime(rubyClass, rubyClass.getContext());
        }
    }
    public SignalRuntime[] getSelfDependsOn(){
        return selfDependsOn;
    }
    public void setSelfDependsOn(SignalRuntime[] selfDependsOn) {
        this.selfDependsOn = selfDependsOn;
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
