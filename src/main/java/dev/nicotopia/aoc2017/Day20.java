package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day20 {
    private record Vec3i(int x, int y, int z) {
        public Vec3i add(Vec3i right) {
            return new Vec3i(this.x + right.x, this.y + right.y, this.z + right.z);
        }

        public int manhattenLength() {
            return Math.abs(this.x) + Math.abs(this.y) + Math.abs(this.z);
        }
    }

    private static class Particle {
        private Vec3i p;
        private Vec3i v;
        private Vec3i a;

        public Particle(Vec3i p, Vec3i v, Vec3i a) {
            this.p = p;
            this.v = v;
            this.a = a;
        }

        public void tick() {
            this.v = this.v.add(this.a);
            this.p = this.p.add(this.v);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Particle> particles;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day20.class.getResourceAsStream("/2017/day20.txt")))) {
            Pattern p = Pattern.compile(
                    "^p=<(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)>, v=<(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)>, a=<(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)>$");
            particles = br.lines().map(p::matcher).filter(Matcher::matches).map(m -> new Particle(
                    new Vec3i(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3))),
                    new Vec3i(Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5)), Integer.valueOf(m.group(6))),
                    new Vec3i(Integer.valueOf(m.group(7)), Integer.valueOf(m.group(8)), Integer.valueOf(m.group(9)))))
                    .toList();
        }
        int partOne = particles.indexOf(
                particles.stream().min((l, r) -> Integer.compare(l.a.manhattenLength(), r.a.manhattenLength())).get());
        System.out.println("Part one: " + partOne);
        int iterations = 1000; // guess
        for (int i = 0; i < iterations; ++i) {
            List<Particle> old = particles;
            particles = particles.stream().filter(p0 -> old.stream().noneMatch(p1 -> p0 != p1 && p0.p.equals(p1.p)))
                    .toList();
            particles.forEach(Particle::tick);
        }
        System.out.println("Part two: " + particles.size());
    }
}