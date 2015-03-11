require 'bench/benchmarks/signal/lib/signalhelper'

class ChainSFunSignal

  def main()
    self
  end

  def emit(i)
    f0(i)
  end
  def f0(i)
    f1(i)
  end
  def f1(i)
    @v1 = i;
    f2(i)
  end
  def f2(i)
    @v2 = i;
    f3(i)
  end
  def f3(i)
    @v3 = i;
    f4(i)
  end
  def f4(i)
    @v4 = i;
    f5(i)
  end
  def f5(i)
    @v5 = i;
    f6(i)
  end
  def f6(i)
    @v6 = i;
    f7(i)
  end
  def f7(i)
    @v7 = i;
    f8(i)
  end
  def f8(i)
    @v8 = i;
    f9(i)
  end
  def f9(i)
    @v9 = i;
    f10(i)
  end
  def f10(i)
    @v10 = i;
    @res = i;
  end


  def res
      @res
  end

end


def harness_input
  200000
end

def harness_sample(input)
  chain = ChainSFunSignal.new
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
