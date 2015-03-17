require_relative 'rtest'
require_relative 'signalhelper'


test do 
	descript "simpel Behavior test"

	a = source(3)
	b = source(2)

	c = map(a,b) {a.value + b.value}

end