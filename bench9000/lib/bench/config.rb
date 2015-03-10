# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench

  class Config

    attr_reader :implementations
    attr_reader :implementation_groups
    attr_reader :benchmarks
    attr_reader :benchmark_groups
    attr_reader :fail_hard_exclusions
    attr_reader :commands

    def initialize
      @implementations = {}
      @implementation_groups = {}
      @benchmarks = {}
      @benchmark_groups = {}
      @fail_hard_exclusions = []
      @commands = {}
    end

    def load(file)
      self.instance_eval File.read(file)
    end

    def rbenv(name, version=name, flags="")
      @implementations[name] = RbenvImplementation.new(name, version, flags)
    end

    def binary(name, binary, flags="")
      @implementations[name] = BinaryImplementation.new(name, binary, flags)
    end

    def implementation_group(name, *implementations)
      @implementation_groups[name] = Group.new(name, implementations.map { |i|
        implementation = @implementations[i]

        if implementation.nil?
          puts "unknown implementation #{i} in group"
          exit
        end

        implementation
      })
    end

    def default_benchmarks_dir
      File.join(File.dirname(__FILE__), "benchmarks")
    end

    def benchmark(name, file, flags="")
      @benchmarks[name] = Benchmark.new(name, file, flags)
    end

    def benchmark_group(name, *benchmarks)
      @benchmark_groups[name] = Group.new(name, benchmarks.map { |b|
        benchmark = @benchmarks[b]

        if benchmark.nil?
          puts "unknown benchmark #{b} in group"
          exit
        end

        benchmark
      })
    end

    def fails_hard(implementation, benchmark)
      @fail_hard_exclusions.push([implementation, benchmark])
    end

    def command(name, &body)
      command_class = Class.new(Commands::Command)
      command_class.class_eval(&body)
      @commands[name] = command_class
    end

  end

end
