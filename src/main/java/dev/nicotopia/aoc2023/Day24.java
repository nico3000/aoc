package dev.nicotopia.aoc2023;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Mat4x4bi;
import dev.nicotopia.aoc.algebra.Vec2d;
import dev.nicotopia.aoc.algebra.Vec3i64;
import dev.nicotopia.aoc.algebra.Vec4bi;

public class Day24 extends DayBase {
    private record Hailstone(Vec3i64 p, Vec3i64 v) {
        private Optional<Vec2d> getXYIntersectionParameters(Hailstone other) {
            long det = this.v.y() * other.v.x() - this.v.x() * other.v.y();
            if (det == 0) {
                return Optional.empty();
            }
            long a = other.p.x() - this.p.x();
            long b = other.p.y() - this.p.y();
            double r = (double) (-other.v.y() * a + other.v.x() * b) / (double) det;
            double s = (double) (-this.v.y() * a + this.v.x() * b) / (double) det;
            return Optional.of(new Vec2d(r, s));
        }

        private Vec2d xyAt(double t) {
            return new Vec2d((double) this.p.x() + t * (double) this.v.x(),
                    (double) this.p.y() + t * (double) this.v.y());
        }
    }

    private List<Hailstone> hailstones;

    private void processInput() {
        Pattern p = Pattern.compile("(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)\\s*@\\s*(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)");
        this.hailstones = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> IntStream.rangeClosed(1, 6).mapToLong(i -> Long.valueOf(m.group(i))).toArray())
                .map(v -> new Hailstone(new Vec3i64(v[0], v[1], v[2]), new Vec3i64(v[3], v[4], v[5]))).toList();
    }

    private long partOne() {
        double min = this.getDoubleInput("min");
        double max = this.getDoubleInput("max");
        return IntStream.range(0, this.hailstones.size() - 1)
                .mapToLong(i -> IntStream.range(i + 1, this.hailstones.size())
                        .mapToObj(j -> this.hailstones.get(i).getXYIntersectionParameters(this.hailstones.get(j)))
                        .filter(rs -> rs.isPresent() && 0.0 <= rs.get().x() && 0.0 <= rs.get().y())
                        .map(rs -> this.hailstones.get(i).xyAt(rs.get().x()))
                        .filter(p -> min <= p.x() && p.x() <= max && min <= p.y() && p.y() <= max).count())
                .sum();
    }

    private List<Hailstone> selectRandomHailstones(Random r, int n) {
        if (this.hailstones.size() < n) {
            throw new AocException("At least 8 hailstones are needed.");
        }
        Set<Hailstone> selected = new HashSet<>();
        while (selected.size() != 8) {
            selected.add(this.hailstones.get(r.nextInt(this.hailstones.size())));
        }
        return new ArrayList<>(selected);
    }

    private long partTwo() {
        Random rand = new Random();
        List<Hailstone> selected;
        Mat4x4bi m = new Mat4x4bi();
        long b[] = new long[4];
        do {
            selected = this.selectRandomHailstones(rand, 8);
            for (int r = 0; r < 4; ++r) {
                Hailstone h1 = selected.get(2 * r);
                Hailstone h2 = selected.get(2 * r + 1);
                m.setRow(r, h1.v.y() - h2.v.y(), h2.v.x() - h1.v.x(), h2.p.y() - h1.p.y(), h1.p.x() - h2.p.x());
                b[r] = -h1.p.y() * h1.v.x() + h2.p.y() * h2.v.x() + h1.p.x() * h1.v.y() - h2.p.x() * h2.v.y();
            }
        } while (m.det().equals(BigInteger.ZERO));
        Vec4bi r = m.adjugate().mul(new Vec4bi(b[0], b[1], b[2], b[3]));
        long px = r.x().divide(m.det()).longValue();
        long py = r.y().divide(m.det()).longValue();
        long vx = r.z().divide(m.det()).longValue();
        Hailstone hs1 = selected.get(0);
        Hailstone hs2 = selected.get(1);
        long t1 = (px - hs1.p.x()) / (hs1.v.x() - vx);
        long z1 = hs1.p.z() + t1 * hs1.v.z();
        long t2 = (px - hs2.p.x()) / (hs2.v.x() - vx);
        long z2 = hs2.p.z() + t2 * hs2.v.z();
        long vz = (z1 - z2) / (t1 - t2);
        long pz = z1 - t1 * vz;
        return px + py + pz;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("min", 200000000000000L);
        this.pushSecondaryInput("max", 400000000000000L);
        this.addPresetFromResource("Example", "/2023/day24e.txt", 7, 27);
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}