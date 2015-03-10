# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require "mock-logger"

if ENV.include? 'BENCH_9000_NATIVE'
  require "oily_png/oily_png"
  require "psd_native/psd_native"
else
  require "psd/util"
end

module MockUtil
  if ENV.include? 'BENCH_9000_NATIVE'
    extend PSDNative::Util
  else
    extend PSD::Util
  end
end

def micro_harness_input
  14
end

def micro_harness_iterations
  50_000_000
end

def micro_harness_sample(input)
  MockUtil::clamp(input, 10, 20)
end

def micro_harness_expected
  14
end

require 'bench/micro-harness'
