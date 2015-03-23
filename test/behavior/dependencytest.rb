## The following test show more the behavior of the implmentation than to check that it's correct
test do
		describe "dependency in if branche"
		boolVal = signal {true}
		sigD1 = signal { 1 }
		sigD2 = signal { 2 }
		count = 0;
		sigResult = signal { 
				count += 1
				if(boolVal.value)
					sigD1.value
				else
					sigD2.value
				end

		}
		sigD2.emit(3)
		sigD2.emit(4)
		sigD2.emit(5)
		assertEq(1, count) #so far we never enter the else branch => sigResult does not depend on sigD2 
		assertEq(1,sigResult.now)
		boolVal.emit(false) #now sigResult depends on sigD1 and sigD2 
		assertEq(2,count) 
		assertEq(5,sigResult.now)

		sigD1.emit(20) # sigResult depends on sigD1 and D2  
		assertEq(3,count) 
		assertEq(5,sigResult.now)

		sigD2.emit(15)
		assertEq(4,count) 
		assertEq(15,sigResult.now)

end