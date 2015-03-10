# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Reference < Command

      def before(options, existing_measurements)
        if options.implementations.size != 1
          puts "needs exactly one implementation"
          exit 1
        end

        @file = File.open("reference.txt", "w")
        @file.puts "version #{CONFIG_VERSION}"
        @file.flush

        true
      end

      def result(options, b, i, measurement)
        if measurement == :failed
          score = "failed"
        else
          score = measurement.score
        end

        puts "#{b.name} #{score}"
        @file.puts "#{b.name} #{score}"
        @file.flush
      end

      def after(options, measurements)
        @file.close
      end

    end

  end
end
