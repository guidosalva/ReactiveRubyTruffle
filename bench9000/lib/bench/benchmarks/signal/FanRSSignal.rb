require 'bench/benchmarks/signal/lib/rsbehavior'

class Fan

  def main()

    sigSource = srsignalSource 0
    sig1 = srsignal { sigSource.value }
    sig21 = srsignal { sig1.value }
    sig22 = srsignal { sig1.value }
    sig23 = srsignal { sig1.value }

    sig211 = srsignal { sig21.value }
    sig212 = srsignal { sig21.value }
    sig213 = srsignal { sig21.value }

    sig221 = srsignal { sig22.value }
    sig222 = srsignal { sig22.value }
    sig223 = srsignal { sig22.value }

    sig231 = srsignal { sig23.value }
    sig232 = srsignal { sig23.value }
    sig233 = srsignal { sig23.value }


    sigCollect = srsignal {
      sig211.value + sig212.value + sig213.value + sig221.value + sig222.value + sig223.value + sig231.value + sig232.value + sig233.value
    }
    srsignal {
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
