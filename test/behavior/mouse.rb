class Mouse
	 @mouse


	 def initialize(value)
	 	extend BehaviorCore
	 	@value = source(value)
	 end


	 def change(value)
	 	@value.emit(value)
	 end

	 def asBehavior()
	 	return @value
	 end
end

class MouseData
	attr_reader :x, :y, :s

	def initiailze(x,y,s)
		@x = x
		@y = y
		@s = s
	end

	def x=(x)
		MouseData.new(x,@y,@s)
	end

	def y=(y)
		MouseData.new(@x,y,@s)
	end

	def s=(s)
		MouseData.new(@x,@y,s)
	end

end