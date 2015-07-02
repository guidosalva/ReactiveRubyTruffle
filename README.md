# Reactive Ruby - Truffle Ruby meets Reactive Programming

Reactive Ruby is an experimental reactive language, which extends Truffle Ruby. It is implemented as an extension of the Truffle Ruby interpreter.

Reactive Ruby should be executed on the Graal VM instead of the normal Java VM.


## Installation

**Install the Graal VM**

A guide on how to install the Graal VM:
https://wiki.openjdk.java.net/display/Graal/Instructions

**Add Truffle to your local maven repository**
```
hg clone http://lafo.ssw.uni-linz.ac.at/hg/truffle/
cd truffle
./mx.sh build
./mx.sh maven-install-truffle
```

**Reactive Ruby**
```
git clone git@github.com:viering/RRuby.git
cd RRuby
mvn
```
## Usage

The jt tool can be used to execute RRuby program. 
(See: https://github.com/jruby/jruby/tree/master/truffle#workflow-tool)


The following program prints the current time every second
```
time = timeB(1) 
time.onChange { |x| puts x}
```
This example can be executed with:
```
jt run --graal examples/time.rb
```

The following example is a bit more interesting. 
In it we demonstrate some manipulation which can be performed on behaviors.

```
rangeB = range(1,100)

everyFifth = rangeB.filter(rangeB.now) { | x |
	x.to_i % 5 == 1
}


#collect up to 5 numbers
collect = rangeB.fold( [] ) { |acc, val|
	if (acc.size < 5)
		acc.clone << val
	else
		acc = [val]
	end
}


#combine everyFifth and collect
combine = everyFifth.map(collect) { |x, y | [x,y] }

#print output
puts "Every fith value: #{combine.now[0]} \t collected values: #{combine.now[1]}"
combine.onChange { |x|
	puts "Every fith value: #{x[0]} \t collected values: #{x[1]}"
	}

```

The output is:

```
Every fith value: 1 	 collected values: [1]
Every fith value: 1 	 collected values: [1, 2]
Every fith value: 1 	 collected values: [1, 2, 3]
Every fith value: 1 	 collected values: [1, 2, 3, 4]
Every fith value: 1 	 collected values: [1, 2, 3, 4, 5]
Every fith value: 6 	 collected values: [6]
Every fith value: 6 	 collected values: [6, 7]
Every fith value: 6 	 collected values: [6, 7, 8]
Every fith value: 6 	 collected values: [6, 7, 8, 9]
Every fith value: 6 	 collected values: [6, 7, 8, 9, 10]
...
```
This example program can be executed with:
```
jt run --graal examples/range.rb
```


