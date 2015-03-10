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
  require "chunky_png"
end

PIXEL = 0x12345678

class MockCanvas
  if ENV.include? 'BENCH_9000_NATIVE'
    include OilyPNG::Operations
  else
    include ChunkyPNG::Canvas::Operations
  end

  public :compose!

  def initialize
    @pixels = Array.new(width * height)

    @pixels.size.times do |n|
      @pixels[n] = PIXEL
    end
  end

  def width
    2000
  end

  def height
    2000
  end

  def pixels
    @pixels
  end

  def get_pixel(x, y)
    @pixels[y * width + x]
  end

  def set_pixel(x, y, color)
    @pixels[y * width + x] = color
  end
end

def harness_input
  [MockCanvas.new, MockCanvas.new]
end

def harness_sample(input)
  onto, compose = input
  onto.compose!(compose, 0, 0)
  onto
end

def harness_verify(output)
  output.pixels.inject(:+) == 882178784000000
end

require 'bench/harness'
