package org.jruby.truffle.runtime.signalRuntime;

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
}
