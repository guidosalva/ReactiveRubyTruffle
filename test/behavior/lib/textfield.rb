class Textfield
	 @value


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