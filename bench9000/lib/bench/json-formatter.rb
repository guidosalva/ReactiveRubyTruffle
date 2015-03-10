# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require 'json'

module Bench

  module JSONFormatter

    def self.format(options, measurements)
        benchmarks = options.benchmarks
        implementations = options.implementations

        JSON.pretty_generate({
          benchmarks: benchmarks,
          implementations: implementations,
          measurements: benchmarks.product(implementations).map do |b, i|
            measurement = measurements[b, i]

            if measurement == :failed
              {
                benchmark: b,
                implementation: i,
                failed: true
              }
            else
              {
                benchmark: b,
                implementation: i,
                warmup_time: measurement.warmup_time,
                sample_mean: measurement.sample_mean,
                sample_error: measurement.sample_error,
                score: measurement.score,
                score_error: measurement.score_error,
                warmup_samples: measurement.warmup_samples,
                samples: measurement.samples
              }
            end
          end
        })
    end

  end

end