class Button
	@eventSource;

	def initialize
		extend BehaviorCore
		@eventSource = source(:noClick)
	end

	def click()
		@eventSource.emit(:click)
		@eventSource.emit(:noClick)
	end

	def asBehavior()
	 	return @eventSource
	end
end