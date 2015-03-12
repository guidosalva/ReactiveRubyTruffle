require_relative 'lib/signalobservers'

class Fan

  def main()


    @obSource = SignalSourceObserver.new(0)


    ob11 = SignalObserver.new(@obSource)
    ob12 = SignalObserver.new(@obSource)
    ob13 = SignalObserver.new(@obSource)

    ob111 = SignalObserver.new(ob11)
    ob112 = SignalObserver.new(ob11)
    ob113 = SignalObserver.new(ob11)

    ob121 = SignalObserver.new(ob12)
    ob122 = SignalObserver.new(ob12)
    ob123 = SignalObserver.new(ob12)

    ob131 = SignalObserver.new(ob113)
    ob132 = SignalObserver.new(ob113)
    ob133 = SignalObserver.new(ob113)

    setResOb = SetResObserver.new(self)
    ob111.add_observer(setResOb)
    ob112.add_observer(setResOb)
    ob113.add_observer(setResOb)

    ob121.add_observer(setResOb)
    ob122.add_observer(setResOb)
    ob123.add_observer(setResOb)

    ob131.add_observer(setResOb)
    ob132.add_observer(setResOb)
    ob133.add_observer(setResOb)

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
  fan = Fan.new
  sig = fan.main

  for i in 1 .. harness_input
    sig.emit(i)
  end
  fan.res
end

def harness_verify(output)
  output == harness_input
end

require 'bench/harness'
