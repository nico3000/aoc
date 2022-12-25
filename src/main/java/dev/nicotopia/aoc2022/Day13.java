package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Day13 {
    public record Pair(Packet left, Packet right) {
        public boolean isRightOrder() {
            return this.left.compareTo(this.right) < 0;
        }
    }

    public static class Packet implements Comparable<Packet> {
        private final List<Packet> contents;
        private final Integer value;

        public Packet(int value) {
            this.contents = null;
            this.value = value;
        }

        public Packet(List<Packet> contents) {
            this.contents = new ArrayList<>(contents);
            this.value = null;
        }

        public Packet(String src) {
            if (!src.isBlank() && src.charAt(0) != '[') {
                this.contents = null;
                this.value = Integer.valueOf(src);
            } else {
                this.contents = new ArrayList<>();
                this.value = null;
                int lvl = 0;
                int elementBegin = 1;
                for (int i = 1; i < src.length() - 1; ++i) {
                    switch (src.charAt(i)) {
                        case '[' -> ++lvl;
                        case ']' -> --lvl;
                        case ',' -> {
                            if (lvl == 0) {
                                this.contents.add(new Packet(src.substring(elementBegin, i)));
                                elementBegin = i + 1;
                            }
                        }
                    }
                }
                if (!src.isEmpty()) {
                    this.contents.add(new Packet(src.substring(elementBegin, src.length() - 1)));
                }
            }
        }

        @Override
        public int compareTo(Packet right) {
            if (this == right) {
                return 0;
            } else if (this.value != null && right.value != null) {
                return Integer.compare(this.value, right.value);
            } else if (this.value == null && right.value != null) {
                return this.compareTo(new Packet(Arrays.asList(right)));
            } else if (this.value != null && right.value == null) {
                return new Packet(Arrays.asList(this)).compareTo(right);
            } else {
                for (int i = 0; i < Math.min(this.contents.size(), right.contents.size()); ++i) {
                    int cmp = this.contents.get(i).compareTo(right.contents.get(i));
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return Integer.compare(this.contents.size(), right.contents.size());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Pair> pairs = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day13.class.getResourceAsStream("/2022/day13.txt")))) {
            do {
                String firstStr = br.readLine();
                String secondStr = br.readLine();
                pairs.add(new Pair(new Packet(firstStr), new Packet(secondStr)));
            } while (br.readLine() != null);
        }
        int sum = IntStream.range(1, pairs.size() + 1).filter(i -> pairs.get(i - 1).isRightOrder()).sum();
        System.out.println("Part one: " + sum);
        Packet p0 = new Packet("[[2]]");
        Packet p1 = new Packet("[[6]]");
        pairs.add(new Pair(p0, p1));
        List<Packet> sorted = pairs.stream().mapMulti((Pair p, Consumer<Packet> m) -> {
            m.accept(p.left);
            m.accept(p.right);
        }).sorted().toList();
        System.out.println("Part two: " + ((sorted.indexOf(p0) + 1) * (sorted.indexOf(p1) + 1)));
    }
}