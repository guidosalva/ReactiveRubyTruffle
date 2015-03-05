
class DataClass
		def initialize(v)
			@a = v
		end
		def getA
			@a
		end
		def setA (v)
			@a = v
		end
end

describe "Behavior" do 
	it "bla bla bla" do
		sig = Behavior.new { 1 } 
		expect(sig.now).to eq (1)
	end
	
end
