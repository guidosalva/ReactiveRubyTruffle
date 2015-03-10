# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Score < Command

      def result(options, b, i, measurement)
        if measurement == :failed
          score = "failed"
        else
          score = measurement.score
        end

        puts "#{b} #{i} #{score}"
      end

    end

  end
end
