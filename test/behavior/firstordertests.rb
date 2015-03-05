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

test do
		descript "no signal recomputation if only normal vars change"
		d = DataClass.new(3)
		sigA = signal {1}
		sigB = signal {d.getA + sigA.value}
		assertEq(4,sigB.now)
		d.setA 5
		assertEq(4,sigB.now)
		sigA.emit 2
		assertEq(7,sigB.now)
end
