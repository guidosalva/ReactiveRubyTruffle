require 'singleton'
class Label
	 @x
	 @y

	 def initialize(x,y)
	 	@x = x
	 	@y = y
	 end

	 def get_x
	 	@x
	 end

	 def get_y
	 	@y
	 end

	 def set_x(x)
	 	@x = x
	 end

	 def set_y (y)
	 	@y = y
	 end
end
class NullLable
	 include Singleton


	 def get_x
	 	0
	 end

	 def get_y
	 	0
	 end

	 def set_x(x)
	 end

	 def set_y (y)
	 end
end