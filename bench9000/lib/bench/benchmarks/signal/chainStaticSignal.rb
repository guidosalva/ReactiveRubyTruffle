def source(value)
  BehaviorSource.new(value)
end
def map(basedOn, &block)
  BehaviorSimple.new(basedOn, &block)
end
def map(*basedOn, &block)
  BehaviorSimple.new(*basedOn, &block)
end
class ChainSignal

  def main()

    sigSource = source(0)
    sig1 = map(sigSource) { |x| x}
    sig2 = map(sig1) { |x| x}
    sig3 = map(sig2) { |x| x}
    sig4 = map(sig3) { |x| x}
    sig5 = map(sig4) { |x| x}
    sig6 = map(sig5) { |x| x}
    sig7 = map(sig6) { |x| x}
    sig8 = map(sig7) { |x| x}
    sig9 = map(sig8) { |x| x}
    sig10 = map(sig9) { |x| @res = x}
    sigSource
  end

  def res
      @res
  end

end


def harness_input
  200000
end

def harness_sample(input)
  chain = ChainSignal.new
  sig = chain.main

  for i in 1 .. harness_input
    sig.emit(i)
  end
  chain.res
end

def harness_verify(output)
  output == harness_input
end

require 'bench/harness'
