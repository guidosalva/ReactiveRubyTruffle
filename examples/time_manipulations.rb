require_relative('lib/time')

#timeB returns a behavior which represents the current time and is updated every 1 second	
time = timeB(1)

#convert form a data object to a integer
uTime = time.map {|x|
	x.to_i
}


#filter out every odd number
evenSeconds = uTime.filter(uTime.now) { | x | 
	x % 2 == 0
}

#collect up to 5 numbers
collect = uTime.fold( [] ) { |acc, val| 
	if (acc.size <= 5)
		acc.clone << val
	else
		acc = []
	end
}


#combine eventSeconds and collect
combine = evenSeconds.map(collect) { |x, y | [x,y]}


#print output
combine.onChange { |x| 
	puts "Even sec: #{x[0]} \t collected values: #{x[1]}"
	}


sleep 100
