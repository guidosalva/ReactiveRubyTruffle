class RTest
	@@totalCount = 0
	@@failCount = 0
	@fail = false
	@msg = ''

	def initialize(&block)	
		begin
			instance_eval(&block)
		rescue Exception => e 
			@fail = true 
			@msg = e.message
			@@failCount = @@failCount + 1;
		end
		@@totalCount = @@totalCount +1;
		printTestResutl
	end

	def assert_equal(a ,b)
		if(a != b) 
			rise 'assert Eq failed. expected #{a} but got #{b}'
		else
		end
	end
	def assertEq(a ,b)
		if(a != b) 
			rise 'assert Eq failed. expected #{a} but got #{b}'
		else
		end
	end	

	def descript(text) 
		@testText = text
	end

	def printTestResutl
		if(@fail)
			print "Result for: #{@testText} is: \033[31m fail with: #{@msg}\e[0m\n"
		else
			print "Result for: #{@testText} is: \033[32m success\e[0m\n"
		end
	end

	def self.totalCount
		@@totalCount
	end
	def self.totalFail
		 @@totalFail
	end

end

def test(&init)
	RTest.new &init
end
