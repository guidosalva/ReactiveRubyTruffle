package org.jruby.truffle.runtime.signalRuntime;

import com.oracle.truffle.api.CompilerDirectives;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.objects.Allocator;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.RubyBasicObject;
import org.jruby.truffle.runtime.core.RubyClass;

/**
 * Created by me on 25.02.15.
 */
public class SignalRuntime extends RubyBasicObject {
    public SignalRuntime(RubyClass rubyClass, RubyContext context) {
        super(rubyClass, context);
    }



    public static class SignalRuntimeAllocator implements Allocator {
        @Override
        public SignalRuntime allocate(RubyContext context, RubyClass rubyClass, RubyNode currentNode) {

            return new SignalRuntime(rubyClass,rubyClass.getContext());
        }

    }
}
