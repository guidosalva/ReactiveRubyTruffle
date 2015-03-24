require_relative 'rtest'
#require_relative 'signalhelper'
extend BehaviorCore

test do
		describe "static: simpel Behavior test".bold
		sig = source(1)
		assertEq(1,sig.now)
	end

test do
		describe "static: read signal value".bold
		sigA = source(1)
		sigB = map(sigA) {sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		describe "static: test emit chain".bold
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
		describe "static: signal can have side effects".bold
		sigA = source(1)
		d = 1
		sigB = map(sigA) { d = sigA.value}
		assertEq(1,d)
		sigA.emit(2)
		assertEq(2,d)
end

test do
        describe "static: complex signal update behavior, this test shows why non dynamic dependency discovery would be hard".bold

        class SignalTest

        	def initialize()
        	    extend BehaviorCore
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
		describe "static: frist class 1".bold
		sigA = source(3);
		assert_equal(3,sigA.now)
		sigB = source(4);
		assert_equal(4,sigB.now)
end

test do
		describe "static: signal addition".bold
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
		describe "static: no signal recomputation if only normal vars change".bold
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
		describe "static: simple propagation check 1".bold
		a = source(1);
		b = map(a) {a.value * 1};
		c = map(a) {a.value}
		d = map(a,b) {b.value + c.value }
		a.emit(2)
		assertEq(4,d.now);
end

test do
		describe "static: simple propagation check 2".bold
		a = source(1);
		b = map(a) {a.value};
		c = map(a,b){a.value + b.value }
		a.emit(2)
		assertEq(4,c.now)
end

test do
		describe "static: glich free".bold
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
		describe "static: no double propagation".bold
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

test do
		describe "static: emit differenct sources".bold
		a = source(1);
		b = source(1);
		c = source(1);
		d = source(1);
		am = map(a) {a.value}
		bm = map(b) {b.value}
		cm = map(c) {c.value}
		dm = map(d) {d.value}
		a.emit(2)
		b.emit(2)
		c.emit(2)
		d.emit(2)
		assertEq(8,am.now + bm.now + cm.now + dm.now)
end

test do
		describe "static: two signal with the same sig expr".bold

		def lengthCheck(source, length)
    		map (source) { source.value.length > length }
  		end

  		vname = source("")
  		nname = source("")
  		rndSource1 = source("")
  		rndSource2 = source("")
  		rndSource3 = source("")

  		vnameCheck = lengthCheck(vname,3)
  		nnameCheck = lengthCheck(nname,3)
		
		rndSource1.emit("")
		rndSource2.emit("")
		rndSource3.emit("")
		vname.emit("aa")
		nname.emit("aaaa")
		assertEq(false,vnameCheck.value)
		assertEq(true,nnameCheck.value)

		rndSource1.emit("")
		rndSource2.emit("")
		rndSource3.emit("")

		nname.emit("aa")
		vname.emit("aaaa")
		assertEq(false,nnameCheck.value)
		assertEq(true,vnameCheck.value)
end
