require 'bench/benchmarks/signal/lib/rsbehavior'

class Fan

  def main()

    sigSource = srsignalSource 0
    sig1 = srsignal { |outer| sigSource.value(outer) }
    sig21 = srsignal { |outer| sig1.value(outer) }
    sig22 = srsignal { |outer| sig1.value(outer) }
    sig23 = srsignal { |outer| sig1.value(outer) }

    sig211 = srsignal { |outer| sig21.value(outer) }
    sig212 = srsignal { |outer| sig21.value(outer) }
    sig213 = srsignal { |outer| sig21.value(outer) }

    sig221 = srsignal { |outer| sig22.value(outer) }
    sig222 = srsignal { |outer| sig22.value(outer) }
    sig223 = srsignal { |outer| sig22.value(outer) }

    sig231 = srsignal { |outer| sig23.value(outer) }
    sig232 = srsignal { |outer| sig23.value(outer) }
    sig233 = srsignal { |outer| sig23.value(outer) }


    sigCollect = srsignal {|outer|
      sig211.value(outer) + sig212.value(outer) + sig213.value(outer) + sig221.value(outer) + sig222.value(outer) + sig223.value(outer) + sig231.value(outer) + sig232.value(outer) + sig233.value(outer)
    }
    srsignal { |outer|
      @res = sigCollect.value(outer)
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
