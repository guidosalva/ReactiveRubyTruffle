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
		sigB = map {sigA.value}
		assert_equal(1,sigB.now)
	end

test do
		describe "static: test emit chain".bold
		sigA = source(1)
		d = 1
		sigB = map {
				sigA.value #add sig dependency
				d
		}

		sigAssert = map {
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
		sigB = map { d = sigA.value}
		assertEq(1,d)
		sigA.emit(2)
		assertEq(2,d)
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
		sigC = map {sigA.value + sigB.value}
		d = 3
		map {
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
		sigB = map {d + sigA.value }
		assertEq(4,sigB.now)
		d = 5
		assertEq(4,sigB.now)
		sigA.emit 2
		assertEq(7,sigB.now)
end

test do
		describe "static: simple propagation check 1".bold
		a = source(1);
		b = map {a.value * 1};
		c = map {a.value}
		d = map {b.value + c.value }
		a.emit(2)
		assertEq(4,d.now);
end

test do
		describe "static: simple propagation check 2".bold
		a = source(1);
		b = map {a.value};
		c = map {a.value + b.value }
		a.emit(2)
		assertEq(4,c.now)
end

test do
		describe "static: glich free".bold
		a = source(1);
		b = map {a.value * 1};
		c = map {a.value}
		d = map{b.value + c.value }
		map  {
			if d.value == 3
				 fail("we have a glich")
			end
		}
		a.emit(2)
end

test do
		describe "static: no double propagation".bold
		a = source(1);
		b = map {a.value};
		c = map {a.value + b.value }
		count = 0
		map{ 
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
		am = map {a.value}
		bm = map {b.value}
		cm = map {c.value}
		dm = map {d.value}
		a.emit(2)
		b.emit(2)
		c.emit(2)
		d.emit(2)
		assertEq(8,am.now + bm.now + cm.now + dm.now)
end

test do
		describe "static: two signal with the same sig expr".bold

		def lengthCheck(source, length)
    		map { source.value.length > length }
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
    assertEq(0,sum.now)
    s1.emit(1)
    s1.emit(2)
    s1.emit(3)
    s1.emit(4)

    assertEq(10,sum.now)
end

test do
    describe "static: fold node sum".bold

    s1 = source(0)
    s2 = map {s1.value}
    sum = s2.fold(0) { |x,y | x+y }
    assertEq(0,sum.now)
    assertEq(0,s2.now)
    s1.emit(1)
    s1.emit(2)
    s1.emit(3)
    s1.emit(4)

    assertEq(10,sum.now)
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

test do
    describe "static: filter".bold

    s = source(1)
    f1 = s.filter(99) {|x| x > 5}
    f2 = f1.filter(99) {|x| x > 10}
    assertEq(99,f1.now)
    assertEq(99,f2.now)

    f1.onChange {|x| assertBigger(x,5)}
    f2.onChange {|x| assertBigger(x,10)}
    countF1 = 0
    countF2 = 0
    f1.onChange {countF1 = countF1 + 1}
    f2.onChange {countF2 = countF2 + 1}

    s.emit(2)
    s.emit(6)
    s.emit(10)
    s.emit(11)
    s.emit(11)   

    s.emit(3)

    assertEq(3,countF1)
    assertEq(1,countF2)

end

test do
    describe "static: no change -> no propagation".bold

    s = source(1)
    count = 0
    s.map {count = count + 1}

    s.emit(2)
    s.emit(6)
    s.emit(10)
    s.emit(11)
    s.emit(11)   

    s.emit(3)
    s.emit(3)

    assertEq(6,count)

end


test do
    describe "static: more complex propagation test. It test mutible change or no change cases".bold

    s1 = source(1)
    s2 = source(1)
    s3 = source(1)
    #{|| }
    c = s1.map(s2) {|s1,s2| s1 + s2}
    
    e = s2.map {|x| x}
    d = c.map(e) {|c,e| c + e }
    a = s1.map(c,d) {|s1, a,d| s1 + a + d}
    b = a.map(d) {|a, d| a + d }
    f = d.map(s3) {|d, s3| d + s3 }

    countChangeCalls = 0
    proc = Proc.new { countChangeCalls = countChangeCalls + 1 }

    a.onChange &proc
    b.onChange &proc
    c.onChange &proc
    d.onChange &proc
    e.onChange &proc
    f.onChange &proc

    s1.emit(10)
    assertEq(33,a.now)
    assertEq(45,b.now)
    assertEq(11,c.now)
    assertEq(12,d.now)
    assertEq(1,e.now)
    assertEq(13,f.now)
    assertEq(5,countChangeCalls)

    s1.emit(10)
    assertEq(33,a.now)
    assertEq(45,b.now)
    assertEq(11,c.now)
    assertEq(12,d.now)
    assertEq(1,e.now)
    assertEq(13,f.now)
    assertEq(5,countChangeCalls)

    countChangeCalls = 0
    s2.emit(100)
    assertEq(330,a.now)
    assertEq(540,b.now)
    assertEq(110,c.now)
    assertEq(210,d.now)
    assertEq(100,e.now)
    assertEq(211,f.now)
    assertEq(6,countChangeCalls)

    s1.emit(10)
    s2.emit(100)
    assertEq(330,a.now)
    assertEq(540,b.now)
    assertEq(110,c.now)
    assertEq(210,d.now)
    assertEq(100,e.now)
    assertEq(211,f.now)
    assertEq(6,countChangeCalls)


    countChangeCalls = 0
    s3.emit(1000)
    assertEq(330,a.now)
    assertEq(540,b.now)
    assertEq(110,c.now)
    assertEq(210,d.now)
    assertEq(100,e.now)
    assertEq(1210,f.now)
    assertEq(1,countChangeCalls)

    s1.emit(10)
    s2.emit(100)
    s3.emit(1000)
    assertEq(330,a.now)
    assertEq(540,b.now)
    assertEq(110,c.now)
    assertEq(210,d.now)
    assertEq(100,e.now)
    assertEq(1210,f.now)
    assertEq(1,countChangeCalls)

end

test do
    describe "static: propagation test no change only on one path".bold

    s1 = source(1)
    a = s1.map { 0 }
    b = s1.map(a) {|s1,a| s1 + a}

    countChangeCalls = 0
    proc = Proc.new { countChangeCalls = countChangeCalls + 1 }

    a.onChange &proc
    b.onChange &proc

    assertEq(0,countChangeCalls)

    s1.emit(5)
    assertEq(1,countChangeCalls)
    assertEq(0,a.now)
    assertEq(5,b.now)

end

test do
    describe "static: propagation test no change only on one path2".bold

    s1 = source(1)
    a = s1.map { 0 }
    b = a.map(s1) {|s1,a| s1 + a}

    countA = 0
    countB = 0
    procA = Proc.new { countA = countA + 1 }
    procB = Proc.new { countB = countB + 1 }

    a.onChange &procA
    b.onChange &procB

    assertEq(0,countA)
    assertEq(0,countB)

    s1.emit(5)
    assertEq(0,countA)
    aEQ("change of b should be executed" ,1,countB)
    assertEq(0,a.now)
    assertEq(5,b.now)

end

test do
    describe "static: merge".bold

    s1 = source(11)
    s2 = source(12)
    s3 = source(13)
    
    m2 = s1.merge(s2,s3)

    assertEq(11,m2.now)
    
    m2.fold(1) { |acc, l|
            #puts "fold: #{acc}  #{l}  #{(l % 10)}"
            assertEq(acc, (l % 10))
            if(acc > 2)
                1
            else
                acc + 1
            end
    }
    s2.emit(102)
    s3.emit(103)
    s1.emit(101)
    s1.emit(101)
    s2.emit(232)
end

test do
    describe "static: mapN".bold

    s1 = source(11)
    s2 = source(12)
    s3 = source(13)

    m2 = s1.map(s2,s3) {|x,y,z| x+y+z}

    assertEq(36,m2.now)

    s1.emit(111)
    assertEq(136,m2.now)
    s2.emit(212)
    assertEq(336,m2.now)
    s3.emit(313)
    assertEq(636,m2.now)
    s1.emit(111.10)
    assertEq(636.10,m2.now)
end

test do
    describe "static: fold".bold

    s1 = source(11)
    
    sum = s1.fold(0) {|acc, x| acc +x}

    assertEq(11,sum.now)

    s1.emit(1)
    s1.emit(2)
    s1.emit(3)
    s1.emit(4)
    s1.emit(5)
    assertEq(26,sum.now)
end


test do
    describe "static: take".bold

    s1 = source(11)
    
    count = 0
    take = s1.take(5)
    assertEq(11,take.now)
    take.map{ count = count + 1}

    s1.emit(2)
    s1.emit(3)
    s1.emit(4)
    s1.emit(4)
    s1.emit(5)
    s1.emit(6)
    s1.emit(7)

    aEQ("take wrong now",5,take.now)
    aEQ("take wrong count, expected 5 got #{count}",5,count)
end

test do
    describe "static: skip".bold

    s1 = source(1)
    
    count = 0
    skip = s1.skip(99,5)
    skip.map{ count = count + 1}

    s1.emit(2)
    s1.emit(3)
    s1.emit(4)
    assertEq(99,skip.now)
    s1.emit(5)
    s1.emit(1)
    s1.emit(2)
    s1.emit(3)

    assertEq(3,skip.now)
    assertEq(4,count)
end

test do
    describe "static: heterogen test".bold
    s1 = source(1)
    s1.fold(1) { |acc, l|
            if( acc == 1)
                assertEq(1,l)
            else if (acc == 2)
                assertEq("aStr",l)
            else
                assertEq(0.3,l)
            end
                
            end
            if(acc > 2)
                1
            else
                acc + 1
            end
    }
    s1.emit("aStr")
    s1.emit(0.3)
    s1.emit(1)
    s1.emit("aStr")
    s1.emit(0.3)
    s1.emit(1)
end

test do
    describe "static: propagation of object. Changed need to be different for objects".bold
    class Data
        attr_reader :x, :y
        #attr_writer :x, :y

        def initialize(x,y)
            @x = x
            @y = y
        end

        def x=(v)
            return Data.new(v,@y)
        end
        
        def y=(v)
         return Data.new(@x,v)
        end

    end

    d = Data.new()
    d.x=(0)
    d.y=(1)

    s1 = source(d)

    m = s1.map {
        |x| 
        x.y=(x.y + x.x)
        x  
      } 

    d.x=(2)
    s1.emit(d)
    d.x=(3)
    s1.emit(d)

    assertEq(6,d.y)
end


test do
    describe "merge orderring test".bold

    s1 = source(1)
    n1 = s1.map{|x|x*2}
    n2 = s1.map{|x|x*4}
    merge = n1.merge(n2)
    s1.emit(5)
    assertEq(10,merge.now)
end