class Check
	@eventSource;

	def initialize
		extend BehaviorCore
		@eventSource = source(:no)
	end

	def toggle
		if(@eventSource.now == :no)
			@eventSource.emit(:yes)
		else
			@eventSource.emit(:no)
		end
	end

	def check
		@eventSource.emit(:yes)
	end
	def unCheck
		@eventSource.emit(:no)
	end

	def asBehavior()
	 	return @eventSource
	end
end