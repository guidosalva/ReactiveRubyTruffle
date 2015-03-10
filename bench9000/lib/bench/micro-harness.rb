# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

# This file should be kept as simple as possible to accommodate early
# implementations of Ruby.

SMALL_PRIME = 149

def harness_input
  micro_harness_input
end

def harness_sample(input)
  sum = 0

  micro_harness_iterations.times do
    sum = (sum + micro_harness_sample(input)) % SMALL_PRIME
  end

  sum
end

def harness_verify(output)
  expected_output = micro_harness_expected
  expected_sum = 0

  micro_harness_iterations.times do
    expected_sum = (expected_sum + expected_output) % SMALL_PRIME
  end

  output == expected_sum
end

require 'bench/harness'
