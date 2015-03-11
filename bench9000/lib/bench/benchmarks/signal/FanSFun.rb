require 'bench/benchmarks/signal/lib/signalhelper'

class Fan

  def main()
    self
  end
  def emit(v)
    f(v)
  end

  def f(v)
    @res = f11(v) + f12(v) + f13(v)
  end

  def f11(v)
    f111(v)
    f112(v)
    f113(v)
  end
  def f12(v)
    f121(v)
    f122(v)
    f123(v)
  end
  def f13(v)
    f131(v)
    f132(v)
    f133(v)
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
