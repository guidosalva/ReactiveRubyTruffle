require_relative('lib/helper')


rangeB = range(1,100)

everyFifth = rangeB.filter(rangeB.now) { | x | 
	x.to_i % 5 == 1
}


#collect up to 5 numbers
collect = rangeB.fold( [] ) { |acc, val| 
	if (acc.size < 5)
		acc.clone << val
	else
		acc = [val]
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
