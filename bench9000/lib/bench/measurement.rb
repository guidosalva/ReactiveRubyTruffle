# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench

  class Measurement

    attr_reader :warmup_samples
    attr_reader :samples

    attr_reader :warmup_time

    attr_reader :sample_mean
    attr_reader :sample_error

    attr_reader :score
    attr_reader :score_error

    def initialize(warmup_samples, samples)
      @warmup_samples = warmup_samples
      @samples = samples

      @warmup_time = Stats.sum(warmup_samples)
      @sample_mean = Stats.mean(samples)
      @sample_error = Stats.standard_deviation(samples)
      @score = to_score(@sample_mean)
      @score_error = to_score(@sample_mean - @sample_error) - to_score(@sample_mean)
    end

    def to_score(sample)
      1 / sample * 1000
    end

    def total_time
      Stats.sum(warmup_samples + samples)
    end

  end

  class Measurements

    def initialize
      @hash = {}
    end

    def []=(b, i, m)
      @hash[[b, i]] = m
    end

    def [](b, i)
      @hash[[b, i]]
    end

    def measurements
      @hash
    end

    def total_time
      Stats.sum(measurements.values.map { |m|
        if m == :failed
          0
        else
          m.total_time
        end
      })
    end

  end

end
