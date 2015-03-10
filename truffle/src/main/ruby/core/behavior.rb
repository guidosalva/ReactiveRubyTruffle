$outerSignalHack = nil
class Behavior

	def initialize(&sigExpr)
		@sigExpr = sigExpr;
		execSigExpr()
		self
	end



	def execSigExpr()
		#puts 'called exec Sig Expr for'
		#puts self
		oldOuterSig = $outerSignalHack
		$outerSignalHack = self
		@value = @sigExpr.call(self)
		$outerSignalHack = oldOuterSig
	end

	def emit(newValue)
		@value = newValue
		startUpdatePropagation()
	end

	def now
		@value
	end

	def v
	    value()
	end


end

def signal(&init)
	Behavior.new &init
end
def signalSource( val )
    Behavior.new { val }
end