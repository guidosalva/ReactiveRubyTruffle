# Reactive Ruby - Truffle Ruby meets Reactive Programming

Reactive Ruby is an experimental reactive language, which extends Truffle Ruby.

Reactive Ruby should be executed on the Graal VM instead of the normal Java VM.


## Installation

**Install the Graal VM**

A guide on how to install the Graal VM:
https://wiki.openjdk.java.net/display/Graal/Instructions

**Reactive Ruby**
```
git clone git@github.com:guidosalva/ReactiveRubyTruffle.git
cd ReactiveRubyTruffle
mvn
```
## Usage

The jt tool can be used to execute Reactive Ruby programs.
(See: https://github.com/jruby/jruby/tree/master/truffle#workflow-tool)

```jt run --graal examples/time.rb``` executes the following example which prints the current time every second.

```
time = timeB(1) 
time.onChange { |x| puts x}
```

```jt run --graal examples/sum.rb``` executes the next example, which demonstrates some operators.

```
range = rangeB(1,100)

even = range.filter(0) { | x | 
	x.to_i % 2 == 0
}

sum = even.fold( 0 ) { |acc, val| 
	acc + val
}

combine = range.map(sum) { | x, y | [x,y] }

combine.onChange {|x| puts "sum: #{x}"}

```

The output is:

```
sum: [2, 2]
sum: [3, 2]
sum: [4, 6]
sum: [5, 6]
sum: [6, 12]
sum: [7, 12]
sum: [8, 20]
sum: [9, 20]
sum: [10, 30]
...
