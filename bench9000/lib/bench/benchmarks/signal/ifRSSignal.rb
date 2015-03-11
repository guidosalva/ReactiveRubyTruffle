require 'bench/benchmarks/signal/lib/signalhelper'

class IfBench

  def initialize
    main
  end

  def main()

    @sigPathASource = srsignalSource 0
    @sigPathBSource = srsignalSource 0
    @sigIfBool = srsignalSource false;

    #path a is short
    sigPathA1 =  srsignal { @sigPathASource.value }
    #path b is long
    sigPathB1 = srsignal {@sigPathBSource.value }
    sigPathB2 = srsignal {sigPathB1.value }
    sigPathB3 = srsignal {sigPathB2.value }
    sigPathB4 = srsignal {sigPathB3.value }
    sigPathB5 = srsignal {sigPathB4.value }
    sigPathB6 = srsignal {sigPathB5.value }
    sigPathB7 = srsignal {sigPathB6.value }
    sigPathB8 = srsignal {sigPathB7.value }

    sigIf = srsignal {
        if(@sigIfBool)
          sigPathA1.value
        else
          sigPathB8.value
        end
    }
    srsignal {
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
