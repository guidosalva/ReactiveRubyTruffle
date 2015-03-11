require 'bench/benchmarks/signal/lib/signalobservers'

class ChainObs

  def main()

    @obSource = SignalSourceObserver.new(0)
    ob1 = SignalObserver.new(@obSource)
    ob2 = SignalObserver.new(ob1)
    ob3 = SignalObserver.new(ob2)
    ob4 = SignalObserver.new(ob3)
    ob5 = SignalObserver.new(ob4)
    ob6 = SignalObserver.new(ob5)
    ob7 = SignalObserver.new(ob6)
    ob8 = SignalObserver.new(ob7)
    ob9 = SignalObserver.new(ob8)
    setResOb = SetResObserver.new(self)
    ob9.add_observer(setResOb)
    @obSource
  end

  def setRes(v)
    @res = v
  end

  def res
    @res
  end

end

def harness_input
  200000
end

def harness_sample(input)
  chain = ChainObs.new
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
