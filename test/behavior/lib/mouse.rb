class Mouse
	 @value


	 def initialize()
	 	extend BehaviorCore
	 	m = MouseData.new(0,0,:normal)
	 	@value = source( m )
	 end

	 def asBehavior()
	 	return @value
	 end


	def set_x(x)
		@value.emit(@value.now.set_x(x))
	end

	def set_y(y)
		@value.emit(@value.now.set_y(y))
	end

	def mouse_down()
		@value.emit(@value.now.mouse_down)
	end
	def mouse_up()
		@value.emit(@value.now.mouse_up)
	end
	def mouse_normal()
		@value.emit(@value.now.mouse_normal)
	end

	def value
		@value.now
	end
end

class MouseData
	attr_reader :x, :y, :s

	def initialize(x,y,s)
		@x = x
		@y = y
		@s = s
	end

	def set_x(x)
		MouseData.new(x,@y,@s)
	end

	def set_y(y)
		MouseData.new(@x,y,@s)
	end

	def mouse_down()
		MouseData.new(@x,@y,:down)
	end
	def mouse_up()
		MouseData.new(@x,@y,:up)
	end
	def mouse_normal()
		MouseData.new(@x,@y,:normal)
	end

	def status
		@s
	end

end