# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Remove < Command

      def before(options, existing_measurements)
        if options.flags.has_key?("--data")
          file = File.open(options.flags["--data"], "w")
          file.puts "version #{CONFIG_VERSION}"

          existing_measurements.measurements.each do |bi, measurement|
            b, i = bi

            unless options.benchmarks.map(&:name).include?(b) ||
                options.implementations.map(&:name).include?(i)
              if measurement == :failed
                file.puts JSON.generate({benchmark: b, implementation: i, failed: true})
              else
                file.puts JSON.generate({benchmark: b, implementation: i, warmup_samples: measurement.warmup_samples, samples: measurement.samples})
              end
            end
          end

          file.close
        end

        false
      end

    end

  end
end
