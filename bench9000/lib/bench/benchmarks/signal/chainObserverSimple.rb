require 'bench/benchmarks/signal/lib/signalobservers'


class Observer

  def initialize(ob)
    if(!ob.nil?)
      ob.setNext(self)
    end

  end
  def setNext(obs)
    @nextOb = obs
  end
  def callNext(v)
     if(!@nextOb.nil?)
       @nextOb.callNext(v)
     else
       v
     end
  end
end

class ObserverEnd
  def initialize(ob, outer)
    @outer = outer
    if(!ob.nil?)
      ob.setNext(self)
    end

  end

  def callNext(v)
      @outer.setRes(v)
  end
end

class ChainObs

  def main()

    @source = Observer.new(nil)
    ob1 = Observer.new(@source)
    ob2 = Observer.new(ob1)
    ob3 = Observer.new(ob2)
    ob4 = Observer.new(ob3)
    ob5 = Observer.new(ob4)
    ob6 = Observer.new(ob5)
    ob7 = Observer.new(ob6)
    ob8 = Observer.new(ob7)
    ob9 = ObserverEnd.new(ob8,self)
    @source
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
    sig.callNext(i)
  end
  chain.res
end

def harness_verify(output)
  output == harness_input
end

require 'bench/harness'
