require_relative('lib/time')

#timeB returns a behavior which represents the current time and is updated every 1 second	
time = timeB(1)
time.onChange { |x| puts x}


sleep 10 
