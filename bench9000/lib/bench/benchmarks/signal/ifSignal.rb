require 'bench/benchmarks/signal/lib/rsbehavior'

class IfBench

  def initialize
    main
  end

  def main()

    @sigPathASource = signalSource 0
    @sigPathBSource = signalSource 0
    @sigIfBool = signalSource false;

    #path a is short
    sigPathA1 =  signal { @sigPathASource.value }
    #path b is long
    sigPathB1 = signal {@sigPathBSource.value }
    sigPathB2 = signal {sigPathB1.value }
    sigPathB3 = signal {sigPathB2.value }
    sigPathB4 = signal {sigPathB3.value }
    sigPathB5 = signal {sigPathB4.value }
    sigPathB6 = signal {sigPathB5.value }
    sigPathB7 = signal {sigPathB6.value }
    sigPathB8 = signal {sigPathB7.value }

    sigIf = signal {
        if(@sigIfBool)
          sigPathA1.value
        else
          sigPathB8.value
        end
    }
    signal {
      @res = sigIf.value
    }
  end
  def pathASource
    @sigPathASource
  end
  def pathBSource
    @sigPathBSource
  end
  def ifValue
    @sigIfBool
  end

  def res
      @res
  end

end


def harness_input
  200000
end

def harness_sample(input)
  bench = IfBench.new
  bench.ifValue.emit(false)
  bench.ifValue.emit(true)
  for i in 1 .. harness_input
    bench.pathASource.emit(i)
    bench.pathBSource.emit(i)
  end
  bench.res
end

def harness_verify(output)
  output == harness_input
end

require 'bench/harness'
