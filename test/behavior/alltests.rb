require_relative 'rtest'
require_relative 'staticfrp'
require_relative 'formtest'


msg = "\n --- #{RTest.totalFail} of #{RTest.totalCount} failed ---"
if(RTest.totalFail == 0)
	puts msg.green.bold
else
	puts msg.red.bold
end 

msg = " --- #{RTest.totalToFail} of #{RTest.totalToFailCount} of the expected to fail test failed --- \n\n"
if( RTest.totalToFail ==  RTest.totalToFailCount )
	puts msg.magenta
else
	puts msg.red
end