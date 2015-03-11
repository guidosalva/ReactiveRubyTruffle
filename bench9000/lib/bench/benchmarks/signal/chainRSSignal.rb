require 'bench/benchmarks/signal/lib/rsbehavior'

class ChainRSSignal

  def main()

    @sigSource = srsignalSource 0
    sig1 = srsignal { |outer| @sigSource.value(outer) }
    sig2 = srsignal { |outer| sig1.value(outer) }
    sig3 = srsignal { |outer| sig2.value(outer) }
    sig4 = srsignal { |outer| sig3.value(outer) }
    sig5 = srsignal { |outer| sig4.value(outer) }
    sig6 = srsignal { |outer| sig5.value(outer) }
    sig7 = srsignal { |outer| sig6.value(outer) }
    sig8 = srsignal { |outer| sig7.value(outer) }
    sig9 = srsignal { |outer| sig8.value(outer) }
    sig10 = srsignal { |outer| @res = sig9.value(outer) }
    @sigSource
  end

  def res
    @res
  end

end


def harness_input
  200000
end

def harness_sample(input)
  chain = ChainRSSignal.new
  sig = chain.main

  for i in 1 .. harness_input
    sig.emit(i)
  end
  chain.res
end

def harness_verify(output)
  output == harness_input
end

require 'bench/harness'
