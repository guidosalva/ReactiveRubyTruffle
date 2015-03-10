# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require "mock-logger"

require "psd/renderer/mask"

if ENV.include? 'BENCH_9000_NATIVE'
  require "oily_png/oily_png"
  require "psd_native/psd_native"
else
  require "chunky_png/color"
  require "psd/color"
  require "psd/util"
end

SIZE = 2_000
PIXEL = 0x11223344

class MockLayer
  def initialize(image)
    @image = image
  end

  def top
    0
  end

  def left
    0
  end

  def width
    SIZE
  end

  def height
    SIZE
  end

  def name
    "name"
  end

  def mask
    self
  end

  attr_reader :image
end

class MockCanvas
  def initialize
    @sum = 0
    @pixels = [PIXEL] * SIZE * SIZE
  end

  def top
    0
  end

  def left
    0
  end

  def width
    SIZE
  end

  def height
    SIZE
  end

  def pixels
    @pixels
  end

  def [](x, y)
    @pixels[y * SIZE + x]
  end

  def []=(x, y, pixel)
    @pixels[y * SIZE + x] = pixel
  end

  def include_xy?(x, y)
    true
  end

  def sum
    @sum
  end

  def canvas
    self
  end
end

class MockMask < PSD::Renderer::Mask
  if ENV.include? 'BENCH_9000_NATIVE'
    include PSDNative::Renderer::Mask
  end

  public :apply!

  def initialize
    @layer = MockLayer.new(self)
    @canvas = MockCanvas.new
    @mask_data = [0x12] * SIZE * SIZE
    @doc_width = SIZE
    @doc_height = SIZE
  end

  attr_reader :node
  attr_reader :canvas
  attr_reader :mask
end

def harness_input
  MockMask.new
end

def harness_sample(input)
  input.apply!
  input
end

def harness_verify(output)
  output.canvas.pixels.inject(:+) == 1149815824000000
end

require 'bench/harness'
