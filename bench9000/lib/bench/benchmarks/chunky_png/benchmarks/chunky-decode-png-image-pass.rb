# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

class Set
end

class SortedSet < Set
end

require "chunky_png"

if ENV.include? 'BENCH_9000_NATIVE'
  require "oily_png/oily_png"
else
  require "chunky_png"
end

WIDTH = 400
HEIGHT = 400
COLOR_MODE = ChunkyPNG::COLOR_TRUECOLOR_ALPHA
DEPTH = 8
PIXEL = 0x12345678

class MockCanvas
  extend ChunkyPNG::Canvas::PNGDecoding
  if ENV.include? 'BENCH_9000_NATIVE'
    extend OilyPNG::PNGDecoding
  else
    extend ChunkyPNG::Canvas::PNGDecoding
  end

  class << self
    public :decode_png_image_pass
  end

  def initialize(width, height, pixels)
    @width = width
    @height = height
    @pixels = pixels
  end

  def width
    @width
  end

  def height
    @height
  end

  def pixels
    @pixels
  end
end

def harness_input
  pixel = [PIXEL].pack("N")
  scan_line = [ChunkyPNG::FILTER_NONE].pack("c") + (pixel * WIDTH)
  scan_line * HEIGHT
end

def harness_sample(input)
  MockCanvas::decode_png_image_pass(input, WIDTH, HEIGHT, COLOR_MODE, DEPTH, 0)
end

def harness_verify(output)
  output.pixels.inject(:+) == 48867183360000
end

require 'bench/harness'
