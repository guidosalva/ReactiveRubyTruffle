def timeB(sec)
		extend BehaviorCore
		time = source(Time.now)
		Thread.new do
			while true do
				time.emit(Time.now)
				sleep sec
			end
		end
		return time.map{|x|x}
end

#class TimeB	
#	def initialize
#		extend BehaviorCore
#		@time = source(Time.now)
#		Thread.new do
#			while true do
#				@time.emit(Time.now)
#				sleep 1
#			end
#		end
#
#	end
#
#	def asBehavior
#		return @time.map{|x|x}
#	end
#
#end