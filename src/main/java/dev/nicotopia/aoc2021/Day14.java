package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day14 {
    public static void main(String args[]) throws IOException {
        String sequence;
        SequenceExpander expander = new SequenceExpander(40);
        expander.expand("AB", 1);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day14.class.getResourceAsStream("/2021/day14.txt")))) {
            sequence = br.readLine();
            Pattern p = Pattern.compile("^(\\w{2}) -> (\\w)$");
            br.lines().map(p::matcher).filter(Matcher::matches)
                    .forEach(m -> expander.addRule(m.group(1), m.group(2).charAt(0)));
        }
        expander.expand(sequence, 10);
        expander.expand(sequence, 40);
    }

    private static class SequenceExpander {
        private final int[][] rules = new int[26][26];
        private final long histogram[] = new long[26];
        private final long[][][][] knownHistograms;

        public SequenceExpander(int maxDepth) {
            this.knownHistograms = new long[maxDepth][][][];
        }

        public void addRule(String charPair, char c) {
            this.rules[charPair.charAt(0) - 'A'][charPair.charAt(1) - 'A'] = c - 'A';
        }

        public void expand(String sequence, int maxDepth) {
            long begin = System.nanoTime();
            Arrays.fill(this.histogram, 0);
            for (int i = 0; i < sequence.length(); ++i) {
                ++this.histogram[sequence.charAt(i) - 'A'];
            }
            for (int i = 0; i < maxDepth; ++i) {
                this.knownHistograms[i] = new long[26][26][];
            }
            for (int i = 0; i < sequence.length() - 1; ++i) {
                this.expand(sequence.charAt(i) - 'A', sequence.charAt(i + 1) - 'A', 0, maxDepth, this.histogram);
            }
            List<Integer> sorted = IntStream.range(0, 26).filter(i -> this.histogram[i] != 0).boxed()
                    .sorted((l, r) -> Long.compare(this.histogram[l], this.histogram[r])).toList();
            long end = System.nanoTime();
            System.out.printf("score after %d steps (elapsed time: %.3f ms): %d.\n", maxDepth, 1e-6f * (end - begin),
                    this.histogram[sorted.get(sorted.size() - 1)] - this.histogram[sorted.get(0)]);
        }

        private void expand(int leftChar, int rightChar, int currentDepth, int maxDepth, long[] histogram) {
            if (currentDepth != maxDepth) {
                long knownHistogram[] = this.knownHistograms[currentDepth][leftChar][rightChar];
                if (knownHistogram == null) {
                    knownHistogram = this.knownHistograms[currentDepth][leftChar][rightChar] = new long[26];
                    int c = this.rules[leftChar][rightChar];
                    if (c != 0) {
                        knownHistogram[c] = 1;
                        this.expand(leftChar, c, currentDepth + 1, maxDepth, knownHistogram);
                        this.expand(c, rightChar, currentDepth + 1, maxDepth, knownHistogram);
                    }
                }
                for (int i = 0; i < histogram.length; ++i) {
                    histogram[i] += this.knownHistograms[currentDepth][leftChar][rightChar][i];
                }
            }
        }
    }
}