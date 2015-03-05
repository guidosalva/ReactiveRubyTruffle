test do 
	descript "higher oder signal behavior"
	sigDown = signal { false }
	sigMove = signal { 1 }
	ssDnD = signal { 
			if( sigDown.value() )
				sigMove
			else
				signal { :"no move"}
			end
	}

	array = Array.new(0)
	#sigD = signal { puts "sig d : #{ssDnD.value.value}"}
	sigFillArray = signal { array << ssDnD.value.value}
	sigMove.emit(1)
	sigMove.emit(2)
	sigMove.emit(3)
	sigMove.emit(4)
	sigDown.emit(true)
	sigMove.emit(5)
	sigMove.emit(6)
	sigMove.emit(7)
	sigMove.emit(8)
	assertEq(array[0], :"no move")
	assertEq(4,array[1])
	assertEq(8,array[5])
	assertEq(6, array.count())
end