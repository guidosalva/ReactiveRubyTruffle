require 'test/unit'

class SimpleTests < Test::Unit::TestCase
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


	def test_createSignal
		sig = Behavior.new { 1 } 
		assert_equal(1,sig.now)
	end

	def test_readSignalValue
		sigA = Behavior.new { 1 }
		sigB = Behavior.new{ sigA.value}
		assert_equal(1,sigB.now)
	end

	def test_emitChain

		sigA = Behavior.new{ 1 }
		data d = DataClass.new{sigA.now}
		sigB = Behavior.new{
				sigA.value #add sig dependency
				d.getA
		}
		sigAssert = Behavior.new{
			assert_equal(d.getA,sigB.value)
		}
		for i in 1 .. 10
			d.setA(i)
			sigA.emmit(i) 
		end
	end
	


	
end
