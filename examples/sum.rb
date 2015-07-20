require_relative('lib/helper')

range = rangeB(1,100)

even = range.filter(0) { | x | 
	x.to_i % 2 == 0
}

sum = even.fold( 0 ) { |acc, val| 
	acc + val
}

combine = range.map(sum) { | x, y | [x,y] }

combine.onChange {|x| puts "sum: #{x}"}

sleep 100