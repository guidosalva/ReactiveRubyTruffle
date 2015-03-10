# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require "mock-logger"

require "psd/image_formats/rle"

if ENV.include? 'BENCH_9000_NATIVE'
  require "oily_png/oily_png"
  require "psd_native/psd_native"
else
  require "chunky_png/color"
  require "psd/color"
  require "psd/util"
end

SIZE = 2500
BYTES = ["\x04", "\x12", "\x34", "\x56", "\x78", "\x90", "\xFB", "\x11"]
BYTE_SEQUENCE = "\x12\x34\x56\x78\x90"

class MockFile
  def initialize
    @position = 0
  end

  def tell
    @position
  end

  def read(n)
    if n == 1
      value = BYTES[@position % BYTES.size]
      @position += 1
      value
    else
      @position += n
      BYTE_SEQUENCE
    end
  end
end

class MockImage
  include PSD::ImageFormat::RLE

  if ENV.include? 'BENCH_9000_NATIVE'
    include PSDNative::ImageFormat::RLE
  else
  end

  public :decode_rle_channel

  def initialize
    @byte_counts = [SIZE] * height
    @line_index = 0
    @chan_pos = 0
    @file = MockFile.new
    @channel_data = [0]
  end

  def height
    SIZE
  end

  def channel_data
    @channel_data
  end
end

def harness_input
  MockImage.new
end

def harness_sample(input)
  input.decode_rle_channel
  input
end

def harness_verify(output)
  output.channel_data.inject(:+) == 408464898
end

require 'bench/harness'
