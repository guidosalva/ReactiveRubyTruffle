require_relative "observer"

module SignalObserverModule
  include Observable
  def value
    @value
  end
  def update(v)
    calc_value(v)
    changed
    notify_observers(@value)
  end
end
class SignalSourceObserver
  include SignalObserverModule
  def initialize(value)
    @value = value
  end

  def changeValue(newValue)
    @value = newValue
    changed
    notify_observers(@value)
  end

end

class ColectValueObserver
  include SignalObserverModule

  def initialize(observable)
    @value = observable.value
    observable.add_observer(self)
  end

  def calc_value(v)
    @value = v
  end
end

class SignalObserver
  include SignalObserverModule

  def initialize(observable)
    @value = observable.value
    observable.add_observer(self)
  end

  def calc_value(v)
    @value = calc(v)
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
