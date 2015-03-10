# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class CompareReference < Command

      def before(options, existing_measurements)
        @reference_scores = read_reference_scores
        true
      end

      def benchmark_complete(options, b, measurements)
        reference_score = @reference_scores[b.name]

        if reference_score.nil?
          puts "reference for #{b} missing"
          return
        end

        puts "#{b} " + options.implementations.map { |i|
          if reference_score == :failed
            if measurements[b, i] == :failed
              "reference and new failed"
            else
              "reference failed: new score: #{measurements[b, i].score}"
            end
          else
            Stats.format_percent(measurements[b, i].score / reference_score)
          end
        }.join(" ")
      end

      def read_reference_scores
        reference_scores = {}

        reference_file = File.open("reference.txt", "r")

        if reference_file.gets.strip != "version #{CONFIG_VERSION}"
          puts "the benchmarks have changed since this file was created"
          exit 1
        end

        reference_file.each do |line|
          benchmark, score = line.split
          
          if score == "failed"
            score = :failed
          else
            score = score.to_i
          end

          reference_scores[benchmark] = score
        end

        reference_scores
      end

    end

  end
end
