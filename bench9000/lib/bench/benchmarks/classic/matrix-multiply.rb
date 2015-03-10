#!/usr/bin/ruby
# -*- mode: ruby -*-
# $Id: matrix.ruby,v 1.2 2005-03-23 06:11:41 bfulgham Exp $
# http://shootout.alioth.debian.org/
#
# Contributed by Christopher Williams
#
# Extracted and modified from https://github.com/jruby/jruby/blob/1.7.16.1/bench/shootout/matrix.ruby
#
# The modifications made are mostly cosmetic. The internal looping was removed since the benchmark system handles
# that.  Matrix creation was dumbed down to work better with nascent Ruby implementations.

SIZE = 175

def mkmatrix(rows, cols)
  count = 0

  matrix = []

  rows.times do
    row = []

    cols.times do
      row << (count += 1)
    end

    matrix << row
  end

  matrix
end

def mmult(rows, cols, m1, m2)
  m3 = []
  for i in 0 .. (rows - 1)
    row = []
    for j in 0 .. (cols - 1)
      val = 0
      for k in 0 .. (cols - 1)
        val += m1[i][k] * m2[k][j]
      end
      row << val
    end
    m3 << row
  end
  m3
end


def harness_input
  m1 = mkmatrix(SIZE, SIZE)
  m2 = mkmatrix(SIZE, SIZE)

  [m1, m2]
end

def harness_sample(input)
  m1, m2 = *input

  mm = mmult(SIZE, SIZE, m1, m2)

  [m1, m2, mm]
end

def harness_verify(output)
  m1, m2, mm = *output

  mm_row_count = mm.size
  mm_col_count = mm.first.size

  m1_row_count = m1.size
  m2_col_count = m2.first.size

  # Verify the dimensionality of the product matrix is correct.
  mm_row_count == m1_row_count && mm_col_count == m2_col_count
end

require 'bench/harness'