# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

# This file should be kept as simple as possible to accommodate early
# implementations of Ruby.

if ARGV[0] == "--loopn"
  ITERATIONS = ARGV[1].to_i
  INNER_ITERATIONS = ARGV[2].to_i

  def micro_harness_iterations
    INNER_ITERATIONS
  end
end

begin
  Process.clock_gettime(Process::CLOCK_MONOTONIC) # test
  def bench9000_get_time
    Process.clock_gettime(Process::CLOCK_MONOTONIC)
  end
rescue Exception
  def bench9000_get_time
    Time.now
  end
end

iteration = 0

while true
  input = harness_input

  start = bench9000_get_time
  actual_output = harness_sample(input)
  time = bench9000_get_time - start

  unless harness_verify(actual_output)
    puts "error"
    exit
  end

  puts time

  # Some implementations of Ruby will not flush each line when writing to a pipe

  STDOUT.flush



  if ARGV[0] == "--loop"
    next
  elsif ARGV[0] == "--loopn"
    iteration += 1
    break if iteration == ITERATIONS
  else
    command = gets
    break unless command == "continue" || command == "continue\n"
  end
end
