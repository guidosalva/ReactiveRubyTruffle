# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

require 'json'

module Bench
  module Commands

    class Detail < Command

      def result(options, b, i, measurement)
        if options.flags.has_key? "--value-per-line"
          if measurement == :failed
            puts "#{b} #{i} failed"
          else
            puts "#{b} #{i} warmup #{measurement.warmup_time}"
            puts "#{b} #{i} sample #{measurement.sample_mean}"
            puts "#{b} #{i} score #{measurement.score}"
          end
        end
      end

      def benchmark_complete(options, b, measurements)
        if options.flags.has_key? "--benchmark-per-line"
          puts "#{b} " + options.implementations.map { |i|
            measurement = measurements[b, i]

            if measurement == :failed
              "failed"
            else
              "#{measurement.warmup_time} #{measurement.sample_mean} #{measurement.score}"
            end
          }.join(" ")
        end
      end

      def after(options, measurements)
        if options.flags.has_key? "--json"
          puts JSON.pretty_generate(JSONFormatter.format(options, measurements))
        end
      end

    end

  end
end
