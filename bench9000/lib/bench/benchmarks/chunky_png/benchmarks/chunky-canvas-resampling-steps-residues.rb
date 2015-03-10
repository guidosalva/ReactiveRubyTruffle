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
  require "chunky_png/canvas/resampling"
end

class MockCanvas
  if ENV.include? 'BENCH_9000_NATIVE'
    include OilyPNG::Resampling
  else
    include ChunkyPNG::Canvas::Resampling
  end

  public :steps_residues
end

def harness_input
  MockCanvas.new
end

def harness_sample(input)
  input.steps_residues(5_000_000, 10_000_000)
end

def harness_verify(output)
  output[0].inject(:+) == 24999990000000 && output[1].inject(:+) == 1275000000
end

require 'bench/harness'
