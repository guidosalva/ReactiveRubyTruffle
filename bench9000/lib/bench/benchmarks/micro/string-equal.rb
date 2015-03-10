def micro_harness_input
  s = "abcdefghij" * 1_000_000
  t = s.dup

  # Ensure to unshare the underlying buffers
  t[0] = 'z'
  t[0] = 'a'

  [s, t]
end

def micro_harness_iterations
  100
end

def micro_harness_sample(input)
  s = input[0]
  t = input[1]

  r = s == t
  r ? 1 : 0
end

def micro_harness_expected
  1
end

require 'bench/micro-harness'
