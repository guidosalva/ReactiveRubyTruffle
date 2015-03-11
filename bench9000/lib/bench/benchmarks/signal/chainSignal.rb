require 'bench/benchmarks/signal/lib/signalhelper'

class ChainSignal

  def main()

    sigSource = signalSource 0
    sig1 = signal { sigSource.value }
    sig2 = signal {sig1.value }
    sig3 = signal {sig2.value }
    sig4 = signal {sig3.value }
    sig5 = signal {sig4.value }
    sig6 = signal {sig5.value }
    sig7 = signal {sig6.value }
    sig8 = signal {sig7.value }
    sig9 = signal {sig8.value }
    sig10 = signal { @res = sig9.value }
    sigSource
  end

  def res
      @res
  end

end


def harness_input
  200000
end

def harness_sample(input)
  chain = ChainSignal.new
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
