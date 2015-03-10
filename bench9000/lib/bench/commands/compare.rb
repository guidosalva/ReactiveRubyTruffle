# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Compare < Command

      def before(options, existing_measurements)
        if options.implementations.size < 2
          puts "you need at least two implementations to compare"
          exit
        end
        true
      end

      def benchmark_complete(options, b, measurements)
        reference = measurements[b, options.implementations[0]]

        if reference == :failed
          reference_info = "(reference failed) "
        else
          reference_info = ""
        end

        puts "#{b} #{reference_info}" + options.implementations.drop(1).map { |i|
          if reference == :failed
            measurements[b, i].score.to_s
          else
            Stats.format_percent(measurements[b, i].score / reference.score)
          end
        }.join(" ")
      end

    end

  end
end
