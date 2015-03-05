require_relative 'rtest'

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
def signal(&init)
	Behavior.new &init
end
def add(a,b)
	Behavior.new do
		a.value + b.value
	end
end

###### Test

test do
		descript "simpel Behavior test"
		sig = Behavior.new { 1 } 
		assertEq(1,sig.now)
	end

test do
		descript "read signal value"
		sigA = Behavior.new { 1 }
		sigB = Behavior.new{ sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		descript "test emmit chain"
		sigA = Behavior.new{ 1 }
		d = DataClass.new(1)
		sigB = Behavior.new{
				sigA.value #add sig dependency
				d.getA
		}
		sigAssert = Behavior.new{
			assert_equal(d.getA,sigB.value)
		}
		for i in 1 .. 10
			d.setA(i)
			sigA.emit(i) 
		end
	end
test do
		descript "frist class 1"
		sigA = signal {3};
		assert_equal(3,sigA.now)
		sigB = signal {4};
		assert_equal(4,sigB.now)
end

test do
		descript "first class sig addition"
		sigA = signal {1};
		sigB = signal {2};
		sigC = add(sigA,sigB)

		d = DataClass.new(3)

		sigTest = signal {
			assert_equal(d.getA,sigC.value)
		}

		d.setA 4
		sigA.emit 2

		d.setA 5
		sigB.emit 3
end
