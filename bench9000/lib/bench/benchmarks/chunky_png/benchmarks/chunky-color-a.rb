# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

if ENV.include? 'BENCH_9000_NATIVE'
  require "oily_png/oily_png"
else
  require "chunky_png/color"
end

module MockColour
  if ENV.include? 'BENCH_9000_NATIVE'
    extend OilyPNG::Color
  else
    extend ChunkyPNG::Color
  end
end

def micro_harness_input
  0x11223344
end

def micro_harness_iterations
  50_000_000
end

def micro_harness_sample(input)
  MockColour::a(input)
end

def micro_harness_expected
  0x44
end

require 'bench/micro-harness'
