package org.jruby.truffle.runtime.signalRuntime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyClass;

/**
 * Created by me on 25.02.15.
 */
public class SignalRuntime extends RubyBasicObject {
    Assumption notIncreasedMaxDepSigs = Truffle.getRuntime().createAssumption("Did not increase # dependent signals");

    private static final int SIZE_DEP_SIG_ARRAY = 2 ;
    @CompilerDirectives.CompilationFinal
    private SignalRuntime[] signalsThatDependOnSelf = new SignalRuntime[SIZE_DEP_SIG_ARRAY];
    @CompilerDirectives.CompilationFinal
    private int maxDependentSigs = SIZE_DEP_SIG_ARRAY;


    private Object sourceInfo = new Object();

    public SignalRuntime(RubyClass rubyClass, RubyContext context) {
        super(rubyClass, context);
    }


    public void addSignalThatDependsOnSelf(SignalRuntime obj) {
        if(notIncreasedMaxDepSigs.isValid()) {
            for (int i = 0; i < signalsThatDependOnSelf.length; i++) {
                if (signalsThatDependOnSelf[i] == null) {
                    signalsThatDependOnSelf[i] = obj;
                    return;
                }
                if (signalsThatDependOnSelf[i] == obj)
                    return;
            }
            resize( obj);
        }
    }

    public SignalRuntime[] getSignalsThatDependOnSelf(){

        return signalsThatDependOnSelf;

    }


    @CompilerDirectives.TruffleBoundary
    private void resize(SignalRuntime obj) {
        notIncreasedMaxDepSigs.invalidate();
        SignalRuntime[] newStore = new SignalRuntime[maxDependentSigs*2];
        System.arraycopy(signalsThatDependOnSelf,0,newStore,0,maxDependentSigs);
        newStore[maxDependentSigs+1] = obj;
        maxDependentSigs *=2;
        signalsThatDependOnSelf = newStore;
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
}
