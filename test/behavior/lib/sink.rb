class Sink

	@value

	def initialize(value)
	 	@value = value
	 end

	def sampleOn(behavior,value)
		behavior.onChange do |x|
			if(x)
				@value = value.now
			end
		end
	end	

	def value
		@value
	end

end