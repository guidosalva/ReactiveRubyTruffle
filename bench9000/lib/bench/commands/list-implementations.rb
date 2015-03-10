# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class ListImplementations < Command

      def before(options, existing_measurements)
        options.config.implementations.values.each do |i|
          puts i.name
        end

        options.config.implementation_groups.values.each do |g|
          puts g.name + ": " + g.members.map(&:name).join(" ")
        end
        
        false
      end

    end

  end
end
