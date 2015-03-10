# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench

  class Options

    attr_reader :config
    attr_reader :implementations
    attr_reader :benchmarks
    attr_reader :flags

    def initialize(
        config,
        implementations,
        benchmarks,
        flags)
      @config = config
      @implementations = implementations
      @benchmarks = benchmarks
      @flags = flags
    end

  end

end
