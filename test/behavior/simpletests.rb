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
		describe "simpel Behavior test"
		sig = signal { 1 } 
		assertEq(1,sig.now)
	end

test do
		describe "read signal value"
		sigA = signal { 1 }
		sigB = signal { sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		describe "test emit chain"
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
		describe "signal can have side effects"
		sigA = signal { 1 }
		d = DataClass.new(0)
		sigB = signal { d.setA(sigA.value)}
		assertEq(1,d.getA)
		sigA.emit(2)
		assertEq(2,d.getA)
end

test do
        describe "complex signal update behavior, this test shows why non dynamic dependency discovery would be hard"

        class SignalTest

        	def initialize()
        		@aSig = signal {1}
        	end


        	def getSig
        		@aSig;
        	end

        	def main()
        	    assertVal = 2
        		a = signal { 1 }
        		b = signal { 	aSig = getSig()
        						a.value + aSig.value}
        		signal { RTest.assertEq(assertVal,b.value)}
        		assertVal = 6
        		a.emit(5)
        		assertVal = 15
        		getSig().emit(10)
        	end

        end

        sigTest = SignalTest.new
        sigTest.main()

end

