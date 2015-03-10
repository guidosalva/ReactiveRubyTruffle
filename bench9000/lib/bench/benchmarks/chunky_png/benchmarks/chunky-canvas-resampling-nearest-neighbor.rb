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

  public :resample_nearest_neighbor!

  def initialize
    @pixels = Array.new(width * height)

    @pixels.size.times do |n|
      @pixels[n] = 0x12345678
    end
  end

  def width
    1000
  end

  def height
    1000
  end

  def pixels
    @pixels
  end

  def get_pixel(x, y)
    @pixels[y * width + x]
  end

  def replace_canvas!(new_width, new_height, pixels)
    @width = new_width
    @height = new_height
    @pixels = pixels
  end
end

def harness_input
  MockCanvas.new
end

def harness_sample(input)
  input.resample_nearest_neighbor!(4000, 4000)
  input
end

def harness_verify(output)
  output.pixels.inject(:+) == 4886718336000000
end

require 'bench/harness'
