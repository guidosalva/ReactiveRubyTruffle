require_relative 'lib/signalobservers'

class IfBench

  def initialize
    main
  end



  def main()

    @sigPathASource = SignalSourceObserver.new(0)
    @sigPathBSource = SignalSourceObserver.new 0
    @sigIfBool = SignalSourceObserver.new false;

    #path a is short
    sigPathA1 =  SignalObserver.new @sigPathASource
    #path b is long
    sigPathB1 = SignalObserver.new @sigPathBSource
    sigPathB2 = SignalObserver.new sigPathB1
    sigPathB3 = SignalObserver.new sigPathB2
    sigPathB4 = SignalObserver.new sigPathB3
    sigPathB5 = SignalObserver.new sigPathB4
    sigPathB6 = SignalObserver.new sigPathB5
    sigPathB7 = SignalObserver.new sigPathB6
    sigPathB8 = SignalObserver.new sigPathB7

    res = IFSignalObserver.new(self,@sigIfBool,sigPathA1,sigPathB8)


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
