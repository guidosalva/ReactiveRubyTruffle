require_relative 'rtest'
require_relative 'simpletests'
require_relative 'higherordertest'
require_relative 'firstordertests'
require_relative 'propagationordertests'

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