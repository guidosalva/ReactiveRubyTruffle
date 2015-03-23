require_relative 'rtest'
require_relative 'signalhelper'
extend BehaviorCore

test do
		descript "static: simpel Behavior test".bold
		sig = source(1)
		assertEq(1,sig.now)
	end

test do
		descript "static: read signal value".bold
		sigA = source(1)
		sigB = map(sigA) {sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		descript "static: test emit chain".bold
		sigA = source(1)
		d = 1
		sigB = map(sigA) {
				sigA.value #add sig dependency
				d
		}

		sigAssert = map(sigB) {
			assert_equal(d,sigB.value)
		}

		for i in 1 .. 10
			d = i
			sigA.emit(i) 
		end
	end

test do	
		descript "static: signal can have side effects".bold
		sigA = source(1)
		d = 1
		sigB = map(sigA) { d = sigA.value}
		assertEq(1,d)
		sigA.emit(2)
		assertEq(2,d)
end

test do
        descript "static: complex signal update behavior, this test shows why non dynamic dependency discovery would be hard".bold

        class SignalTest

        	def initialize()
        		@aSig = source(1)
        	end


        	def getSig
        		@aSig;
        	end

        	def main()
        	    assertVal = 2
        		a = source(1)
        		b = BehaviorSimple.new(getSig(),a) { 
        						aSig = getSig()
        						a.value + aSig.value
        					}
        		BehaviorSimple.new(b) { RTest.assertEq(assertVal,b.value)}
        		assertVal = 6
        		a.emit(5)
        		assertVal = 15
        		getSig().emit(10)
        	end

        end

        sigTest = SignalTest.new
        sigTest.main()

end

test do
		descript "static: frist class 1".bold
		sigA = source(3);
		assert_equal(3,sigA.now)
		sigB = source(4);
		assert_equal(4,sigB.now)
end

test do
		descript "static: signal addition".bold
		sigA = source(1);
		sigB = source(2);
		sigC = map(sigA,sigB) {sigA.value + sigB.value}
		d = 3
		map(sigC) {
			assert_equal(d,sigC.value)
		}
		d = 4
		sigA.emit 2

		d = 5
		sigB.emit 3
end

test do
		descript "static: no signal recomputation if only normal vars change".bold
		d = 3
		sigA = source(1)
		sigB = map(sigA) {d + sigA.value }
		assertEq(4,sigB.now)
		d = 5
		assertEq(4,sigB.now)
		sigA.emit 2
		assertEq(7,sigB.now)
end

test do    
		descript "static: simple propagation check 1".bold
		a = source(1);
		b = map(a) {a.value * 1};
		c = map(a) {a.value}
		d = map(a,b) {b.value + c.value }
		a.emit(2)
		assertEq(4,d.now);
end

test do
		descript "static: simple propagation check 2".bold
		a = source(1);
		b = map(a) {a.value};
		c = map(a,b){a.value + b.value }
		a.emit(2)
		assertEq(4,c.now)
end

test do
		descript "static: glich free".bold
		a = source(1);
		b = map(a) {a.value * 1};
		c = map(a) {a.value}
		d = map(a,b){b.value + c.value }
		map (d) { 
			if d.value == 3
				 fail("we have a glich")	
			end
		}
		a.emit(2)
end

test do
		descript "static: no double propagation".bold
		a = source(1);
		b = map(a) {a.value};
		c = map(a,b) {a.value + b.value }
		count = 0
		map(c){ 
			c.value
			count += 1;
		}
		a.emit(2)
		assertEq(2,count)
end