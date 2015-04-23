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

test do
    describe "static: fold source sum".bold

    s1 = source(0)
    sum = s1.fold(0) { |x,y | x+y }
    s1.emit(1)
    s1.emit(2)
    s1.emit(3)
    s1.emit(4)

    assertEq(10,sum.now)
end

test do
    describe "static: fold node sum".bold

    s1 = source(0)
    s2 = map(s1) {s1.value}
    sum = s2.fold(0) { |x,y | x+y }
    s1.emit(1)
    s1.emit(2)
    s1.emit(3)
    s1.emit(4)

    assertEq(10,sum.now)
end

test do
    describe "static: foldN sum".bold
    s1 = source(1)
    m1 = map(s1) {s1.value}

    s2 = source(2)
    m2 = map(s2) {s2.value}

    s3 = source(3)
    m3 = map(s3) {s3.value}


    sum = m1.foldN(0,m2,m3) { |x,y | x+y }
    assertEq(0,sum.now)
    s1.emit(4)
    assertEq(4,sum.now)
    s2.emit(5)
    assertEq(9,sum.now)
    s3.emit(6)
    assertEq(15,sum.now)
end

test do
    describe "static: foldN sum over sources".bold
    m1 = source(1)
    m2 = source(2)
    m3 = source(3)
    sum = m1.foldN(0,m2,m3) { |x,y | x+y }
    assertEq(0,sum.now)
    m1.emit(4)
    assertEq(4,sum.now)
    m2.emit(5)
    assertEq(9,sum.now)
    m3.emit(6)
    assertEq(15,sum.now)
end

test do
    describe "static: onChange and remove source".bold

    s = source(1)
    n = s
    a1 = 1
    a2 = 1
    a3 = 1
    b1 = Proc.new {|x| assertEq(x,a1)}
    b2 = Proc.new {|x| assertEq(x,a2)}
    b3 = Proc.new {|x| assertEq(x,a3)}

    n.onChange &b1
    a1 = 2
    s.emit(2)

    n.remove &b1
    s.emit(3)
    s.emit(2)

    n.onChange &b1
    n.onChange &b2
    a1 = 3
    a2 = 3
    s.emit(3)

    n.remove &b2
    a1 = 4
    s.emit(4)
    n.remove &b1
    s.emit(5)


    s.emit(3)
    n.onChange &b1
    n.onChange &b2
    n.onChange &b3
    a1 = 6
    a2 = 6
    a3 = 6
    s.emit(6)

    a1 = 7
    a2 = 7
    n.remove &b3
    s.emit(7)
    a1 = 8
    n.remove &b2
    s.emit(8)
    n.remove &b1

    s.emit(9)

end

test do
    describe "static: onChange and remove".bold

    s = source(1)
    n = signal { s.value }
    a1 = 1
    a2 = 1
    a3 = 1
    b1 = Proc.new {|x| assertEq(x,a1)}
    b2 = Proc.new {|x| assertEq(x,a2)}
    b3 = Proc.new {|x| assertEq(x,a3)}

    n.onChange &b1
    a1 = 2
    s.emit(2)

    n.remove &b1
    s.emit(3)
    s.emit(2)

    n.onChange &b1
    n.onChange &b2
    a1 = 3
    a2 = 3
    s.emit(3)

    n.remove &b2
    a1 = 4
    s.emit(4)
    n.remove &b1
    s.emit(5)


    s.emit(3)
    n.onChange &b1
    n.onChange &b2
    n.onChange &b3
    a1 = 6
    a2 = 6
    a3 = 6
    s.emit(6)

    a1 = 7
    a2 = 7
    n.remove &b3
    s.emit(7)
    a1 = 8
    n.remove &b2
    s.emit(8)
    n.remove &b1

    s.emit(9)

end