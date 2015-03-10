# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench

  module Stats

    def self.sum(samples)
      samples.inject(0, :+)
    end

    def self.mean(samples)
      sum(samples) / samples.size
    end

    def self.sample_variance(samples)
      m = mean(samples)
      sum = samples.inject(0) { |a, i| a + (i - m)**2 }
      sum / (samples.length - 1).to_f
    end

    def self.standard_deviation(samples)
      Math.sqrt(sample_variance(samples))
    end

    def self.range(samples)
      samples.max - samples.min
    end

    def self.format_percent(p)
      "#{(p * 100).round(2)}%"
    end

  end

end
