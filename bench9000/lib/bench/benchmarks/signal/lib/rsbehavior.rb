def signal(&init)
  Behavior.new &init
end
def signalSource( val )
  Behavior.new { val }
end

class SRBehavior

  def initialize(&sigExpr)
    @signalDepOnSelf = Hash.new()
    @sigExpr = sigExpr
    execSigExpr()
    self
  end

  def execSigExpr()
    @value = @sigExpr.call(self)
  end

  def emit(newValue)
    @value = newValue
    startUpdatePropagation()
  end

  def startUpdatePropagation()
    #puts "start needs to call #{@signalDepOnSelf}"
    @signalDepOnSelf.keys.each do |key|
      #puts "depends on #{key}"
      key.updatePropagation(self)
    end
  end

  def updatePropagation(source)
    #puts "update #{self} called from #{source}"
    execSigExpr()
    @signalDepOnSelf.keys.each do |key|
      #puts "depends on #{key}"
      key.updatePropagation(self)
    end
  end

  def now
    @value
  end


  def value(outerSignal)
    addDep(outerSignal)
    @value
  end

  def addDep(signal)
    if(!@signalDepOnSelf.has_key?(signal))
      @signalDepOnSelf[signal] = signal
    end
  end



end

def srsignal(&init)
  SRBehavior.new &init
end
def srsignalSource( val )
  SRBehavior.new { val }
end