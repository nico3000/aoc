package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day16 {
    private static class DanceState {
        private final int[] dancers;
        private String labels;
        private int start = 0;

        public DanceState(int size) {
            this.dancers = IntStream.range(0, size).toArray();
            this.labels = Arrays.stream(this.dancers)
                    .collect(StringBuilder::new, (sb, i) -> sb.append((char) ('a' + i)), StringBuilder::append)
                    .toString();
        }

        public void spin(int amount) {
            this.start = (this.start + this.dancers.length - amount % this.dancers.length) % this.dancers.length;
        }

        public void exchange(int a, int b) {
            int posA = (this.start + a) % this.dancers.length;
            int posB = (this.start + b) % this.dancers.length;
            int t = this.dancers[posA];
            this.dancers[posA] = this.dancers[posB];
            this.dancers[posB] = t;
        }

        public void partner(char a, char b) {
            StringBuilder sb = new StringBuilder(this.labels);
            sb.setCharAt(this.labels.indexOf(a), b);
            sb.setCharAt(this.labels.indexOf(b), a);
            this.labels = sb.toString();
        }

        public void permuteLabels(char src[]) {
            for (int i = 0; i < src.length; ++i) {
                src[i] = this.labels.charAt(src[i] - 'a');
            }
        }

        public void permuteIndices(int src[]) {
            int t[] = Arrays.copyOf(src, src.length);
            for (int i = 0; i < src.length; ++i) {
                src[i] = t[this.dancers[(this.start + i) % this.dancers.length]];
            }
        }

        @Override
        public String toString() {
            return IntStream.range(0, this.dancers.length).collect(StringBuilder::new,
                    (sb, i) -> sb.append(this.labels.charAt(this.dancers[(this.start + i) % this.labels.length()])),
                    StringBuilder::append).toString();
        }
    }

    private interface DanceMove {
        public void execute(DanceState s);
    }

    private record Spin(int amount) implements DanceMove {
        @Override
        public void execute(DanceState s) {
            s.spin(this.amount);
        }
    }

    private record Exchange(int a, int b) implements DanceMove {
        @Override
        public void execute(DanceState s) {
            s.exchange(this.a, this.b);
        }
    }

    private record Partner(char a, char b) implements DanceMove {
        @Override
        public void execute(DanceState s) {
            s.partner(this.a, this.b);
        }
    }

    public static void main(String[] args) throws IOException {
        List<DanceMove> danceMoves;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day16.class.getResourceAsStream("/2017/day16.txt")))) {
            danceMoves = Arrays.stream(br.readLine().split(",")).map(m -> (DanceMove) switch (m.charAt(0)) {
                case 's' -> new Spin(Integer.valueOf(m.substring(1)));
                case 'x' -> new Exchange(Integer.valueOf(m.substring(1, m.indexOf('/'))),
                        Integer.valueOf(m.substring(m.indexOf('/') + 1)));
                case 'p' -> new Partner(m.charAt(1), m.charAt(3));
                default -> throw new RuntimeException();
            }).toList();
        }
        DanceState s = new DanceState(16);
        danceMoves.forEach(m -> m.execute(s));
        System.out.println("Part one: " + s.toString());

        int indices[] = IntStream.range(0, 16).toArray();
        char labels[] = IntStream.range(0, 16)
                .collect(StringBuilder::new, (sb, i) -> sb.append((char) ('a' + i)), StringBuilder::append).toString()
                .toCharArray();

        String lastOut = "";
        int iterations = 1000000000;
        long beg = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            s.permuteIndices(indices);
            s.permuteLabels(labels);
            if ((i + 1) % 10000000 == 0) {
                float msPerIteration = (float) (System.currentTimeMillis() - beg) / (float) (i + 1);
                float eta = msPerIteration * (iterations - (i + 1)) / 1000.0f;
                String etaUnit = "s";
                if (60.0f < eta) {
                    eta /= 60.0f;
                    etaUnit = "m";
                }
                if (60.0f < eta) {
                    eta /= 60.0f;
                    etaUnit = "h";
                }
                System.out.print(
                        lastOut.replaceAll(".", "\b") + lastOut.replaceAll(".", " ") + lastOut.replaceAll(".", "\b"));
                lastOut = String.format("%3.0f%%, eta: %.1f %s", 100.0f * (float) (i + 1) / (float) iterations, eta,
                        etaUnit);
                System.out.print(lastOut);
            }
        }
        System.out.println("\nPart two: " + Arrays.stream(indices).collect(StringBuilder::new,
                (sb, i) -> sb.append(labels[i]), StringBuilder::append));
    }
}
