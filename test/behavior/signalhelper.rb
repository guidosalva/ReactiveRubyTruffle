def signal(&init)
	Behavior.new &init
end

def add(a,b)
	Behavior.new do
		a.value + b.value
	end
end

def source(value)
	BehaviorSource.new(value)
end

def map(*basedOn, &block)
	BehaviorSimple.new(*basedOn,&block)
end