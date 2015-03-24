class String
	def black;          "\033[30m#{self}\033[0m" end
	def red;            "\033[31m#{self}\033[0m" end
	def green;          "\033[32m#{self}\033[0m" end
	def brown;          "\033[33m#{self}\033[0m" end
	def blue;           "\033[34m#{self}\033[0m" end
	def magenta;        "\033[35m#{self}\033[0m" end
	def cyan;           "\033[36m#{self}\033[0m" end
	def gray;           "\033[37m#{self}\033[0m" end
	def bg_black;       "\033[40m#{self}\033[0m" end
	def bg_red;         "\033[41m#{self}\033[0m" end
	def bg_green;       "\033[42m#{self}\033[0m" end
	def bg_brown;       "\033[43m#{self}\033[0m" end
	def bg_blue;        "\033[44m#{self}\033[0m" end
	def bg_magenta;     "\033[45m#{self}\033[0m" end
	def bg_cyan;        "\033[46m#{self}\033[0m" end
	def bg_gray;        "\033[47m#{self}\033[0m" end
	def bold;           "\033[1m#{self}\033[22m" end
	def reverse_color;  "\033[7m#{self}\033[27m" end
end
class RTest
	@@totalCount = 0
	@@totalFail = 0
	@@totalToFail = 0
	@@totalToFailCount = 0
	@fail = false
	@shouldFail = false
	@msg = ''


	def initialize(expectedToFail,&block)
			extend BehaviorCore
		@shouldFail = expectedToFail
		begin
			instance_eval(&block)
		rescue Exception => e 
			@fail = true 
			@msg = e.message
			if(expectedToFail)
				@@totalToFail = @@totalToFail +1
			else
				@@totalFail = @@totalFail + 1;
			end
		end
		if(expectedToFail)
			@@totalToFailCount = @@totalToFailCount +1
		else
			@@totalCount = @@totalCount +1;
		end
		
		printTestResutl
	end


	def assert_equal(a ,b)
		if(a != b) 
			raise "assert Eq failed. expected #{a} but got #{b}"
		else
		end
	end
	def assertEq(a ,b)
		if(a != b) 
			raise "assert Eq failed. expected #{a} but got #{b}"
		else
		end
	end

    def self.assertEq(a ,b)
    		if(a != b)
    			raise "assert Eq failed. expected #{a} but got #{b}"
    		else
    		end
    end

	def fail(text)
		raise "faild with #{text}";
	end

	def describe(text)
		@testText = text
	end

	def printTestResutl
		if(@shouldFail)
			if(@fail)
				print "Result for \'#{@testText}\': "
				print "fail -- #{@msg} (this test is expected to fail)\n".magenta
			else
				print "Result for \'#{@testText}\':  "
				print "success(this test is expected to fail) \n".red
			end
		else
			if(@fail)
				print "Result for \'#{@testText}\': \033[31m fail -- #{@msg}\e[0m\n"
			else
				print "Result for \'#{@testText}\': \033[32m success\e[0m\n"
			end
		end

		
	end

	def self.totalCount
		@@totalCount
	end
	def self.totalFail
		 @@totalFail
	end

	def self.totalToFail
		@@totalToFail
	end
	def self.totalToFailCount
		 @@totalToFailCount
	end
	
end

def test(&init)
	RTest.new(false,&init)
end

def test_should_fail(&init)
	RTest.new(true,&init)
end
