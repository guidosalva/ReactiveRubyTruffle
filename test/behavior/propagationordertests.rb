test do
		descript "simple propagation check 1"
		a = signal {1};
		b = signal {a.value * 1};
		c = signal {a.value}
		d = signal {b.value + c.value }
		a.emit(2)
		assertEq(4,d.now);
end

test do
		descript "simple propagation check 2"
		a = signal {1};
		b = signal {a.value};
		c = signal {a.value + b.value }
		a.emit(2)
		assertEq(4,c.now)
end

test_should_fail do
		descript "glich free"
		a = signal {1};
		b = signal {a.value * 1};
		c = signal {a.value}
		d = signal {b.value + c.value }
		signal { 
			if d.value == 3
				 fail("we have a glich")	
			end
		}
		a.emit(2)
end

test_should_fail do
		descript "no double propagation"
		a = signal {1};
		b = signal {a.value};
		c = signal {a.value + b.value }
		count = 0
		signal { 
			c.value
			count += 1;
		}
		a.emit(2)
		assertEq(2,count)
end