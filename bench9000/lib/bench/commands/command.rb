# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Command

      def before(options, existing_measurements)
        true
      end

      def result(options, b, i, measurement)
      end

      def benchmark_complete(options, b, measurement)
      end

      def after(options, measurements)
      end

    end

  end
end
