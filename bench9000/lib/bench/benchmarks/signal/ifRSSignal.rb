require 'bench/benchmarks/signal/lib/rsbehavior'

class IfBench

  def initialize
    main
  end

  def main()

    @sigPathASource = srsignalSource 0
    @sigPathBSource = srsignalSource 0
    @sigIfBool = srsignalSource false;

    #path a is short
    sigPathA1 =  srsignal { |outer| @sigPathASource.value(outer)  }
    #path b is long
    sigPathB1 = srsignal {|outer|@sigPathBSource.value(outer)  }
    sigPathB2 = srsignal {|outer|sigPathB1.value(outer)  }
    sigPathB3 = srsignal {|outer|sigPathB2.value(outer)  }
    sigPathB4 = srsignal {|outer|sigPathB3.value(outer)  }
    sigPathB5 = srsignal {|outer|sigPathB4.value(outer)  }
    sigPathB6 = srsignal {|outer|sigPathB5.value(outer)  }
    sigPathB7 = srsignal {|outer|sigPathB6.value(outer)  }
    sigPathB8 = srsignal {|outer|sigPathB7.value(outer)  }

    sigIf = srsignal { |outer|
        if(@sigIfBool)
          sigPathA1.value(outer)
        else
          sigPathB8.value(outer)
        end
    }
    srsignal { |outer|
      @res = sigIf.value(outer)
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
