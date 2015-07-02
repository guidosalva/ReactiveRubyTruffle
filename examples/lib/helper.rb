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

def range(start,num)
		extend BehaviorCore
		time = source(start)
		Thread.new do
			while time.now <= num - start
				time.emit(time.now + 1)
				sleep 0.5
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