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
  require "psd/color"
  require "psd/util"
end

if ENV.include? 'BENCH_9000_NATIVE'
  def cmyk_to_rgb(c, m, y, k)
    PSDNative::Color::cmyk_to_rgb(c, m, y, k)
  end
else
  def cmyk_to_rgb(c, m, y, k)
    PSD::Color::cmyk_to_rgb(c, m, y, k)
  end
end

def micro_harness_input
  196
end

def micro_harness_iterations
  5_000_000
end

def micro_harness_sample(input)
  rgb = cmyk_to_rgb(input, input, input, input)
  rgb[:r] + rgb[:g] + rgb[:b]
end

def micro_harness_expected
  42
end

require 'bench/micro-harness'
