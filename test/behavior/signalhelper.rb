def signal(&init)
	Behavior.new &init
end

def add(a,b)
	Behavior.new do
		a.value + b.value
	end
end