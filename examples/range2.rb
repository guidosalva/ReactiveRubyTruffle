require_relative('lib/helper')


range = rangeB(1,100)

everyFifth = range.filter(nil) { | x | 
	x.to_i % 5 == 0
}


#collect up to 5 numbers
collect = rangeB.fold( [] ) { |acc, val| 
	if (val.to_i % 5 == 0)
		acc
	else
		acc.clone << val
	end
}


#combine eventSeconds and collect
combine = everyFifth.map(collect) { |x, y | [x,y] }

#print output
puts "Every fith value: #{combine.now[0]} \t collected values: #{combine.now[1]}"
combine.onChange { |x| 
	puts "Every fith value: #{x[0]} \t collected values: #{x[1]}"
	}


sleep 100
