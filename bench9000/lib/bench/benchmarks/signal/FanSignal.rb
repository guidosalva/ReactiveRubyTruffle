require 'bench/benchmarks/signal/lib/signalhelper'

class Fan

  def main()

    sigSource = signalSource 0
    sig1 = signal { sigSource.value }
    sig21 = signal { sig1.value }
    sig22 = signal { sig1.value }
    sig23 = signal { sig1.value }

    sig211 = signal { sig21.value }
    sig212 = signal { sig21.value }
    sig213 = signal { sig21.value }

    sig221 = signal { sig22.value }
    sig222 = signal { sig22.value }
    sig223 = signal { sig22.value }

    sig231 = signal { sig23.value }
    sig232 = signal { sig23.value }
    sig233 = signal { sig23.value }


    sigCollect = signal {
      sig211.value + sig212.value + sig213.value + sig221.value + sig222.value + sig223.value + sig231.value + sig232.value + sig233.value
    }
    signal {
      @res = sigCollect.value;
    }
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
  fan = Fan.new
  sig = fan.main

  for i in 1 .. harness_input
    sig.emit(i)
  end
  fan.res
end

def harness_verify(output)
  output == harness_input * 9
end

require 'bench/harness'
