require_relative "observer"

module SignalObserverModule
  include Observable
  def update(v)
    value = calc_value(v)
    changed
    notify_observers(value)
  end

end
class SignalSourceObserver
  include SignalObserverModule
  def initialize(value)

  end

  def changeValue(newValue)
    value = newValue
    changed
    notify_observers(value)
  end
  def emit(v)
    changeValue(v)
  end

end

class ColectValueObserver
  include SignalObserverModule

  def initialize(observable)
    @value = 0
    observable.add_observer(self)
  end

  def calc_value(v)
    @value = v
  end
  def value
    @value
  end
end

class SignalObserver
  include SignalObserverModule

  def initialize(observable)
    #@value = observable.value
    observable.add_observer(self)
  end

  def calc_value(v)
    calc(v)
  end
  def calc(v)
    v
  end
end
class BinarySignalObserver
  include SignalObserverModule

  def initialize(observable1,observable2)
    @v1 = ColectValueObserver.new(observable1)
    @v2 = ColectValueObserver.new(observable2)
    @v1.add_observer(self)
    @v2.add_observer(self)
  end

  def calc_value(v)
    @value = calc(@v1.value,@v2.value)
  end

  def calc(v1, v2)
    v1 +v2
  end
end
class TripleSignalObserver
  include SignalObserverModule

  def initialize(observable1,observable2,observable3)
    @v1 = ColectValueObserver.new(observable1)
    @v2 = ColectValueObserver.new(observable2)
    @v3 = ColectValueObserver.new(observable3)
    @v1.add_observer(self)
    @v2.add_observer(self)
    @v3.add_observer(self)
  end

  def calc_value(v)
    @value = calc(@v1.value,@v2.value,@v3.value)
  end

  def calc(v1, v2,v3)
    v1 + v2 + v3
  end
end

class NSignalObser
  include SignalObserverModule

  def initialize(size = 5)
    @obs = Array.new(0)
  end
  def addOb(observable)
      tmp = ColectValueObserver.new(observable);
      @obs.push(tmp)
      tmp.add_observer(self)
  end

  def calc_value(v)
    res = 0
    @obs.each do | x|
      res += x.value
    end
    @value = res
  end

end

class SetResObserver
  include SignalObserverModule
  def initialize(main)
    @main= main
  end
  def calc_value(v)
    @main.setRes(v);
  end
end

class IFSignalObserver
  include SignalObserverModule

  def initialize(main,boll,pathA,pathB)
    boll.add_observer(self)
    @pathA = pathA
    @pathB = pathB
    @setRes = SetResObserver.new main
  end

  def calc_value(v)
    if(v)
      @pathB.delete_observer @setRes
      @pathA.add_observer @setRes
    else
      @pathA.delete_observer @setRes
      @pathB.add_observer @setRes
    end

  end

end