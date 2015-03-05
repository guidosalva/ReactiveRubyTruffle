require_relative 'rtest'
require_relative 'signalhelper'

####### Helper defs
class DataClass
		def initialize(v)
			@a = v
		end
		def getA
			@a
		end
		def setA (v)
			@a = v
		end
end


###### Test

test do
		descript "simpel Behavior test"
		sig = signal { 1 } 
		assertEq(1,sig.now)
	end

test do
		descript "read signal value"
		sigA = signal { 1 }
		sigB = signal { sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		descript "test emit chain"
		sigA = signal { 1 }
		d = DataClass.new(1)
		sigB = signal {
				sigA.value #add sig dependency
				d.getA
		}

		sigAssert = signal{
			assert_equal(d.getA,sigB.value)
		}

		for i in 1 .. 10
			d.setA(i)
			sigA.emit(i) 
		end
	end

test do	
		descript "signal can have side effects"
		sigA = signal { 1 }
		d = DataClass.new(0)
		sigB = signal { d.setA(sigA.value)}
		assertEq(1,d.getA)
		sigA.emit(2)
		assertEq(2,d.getA)
end




