package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 {
    public record Cuboid(int w, int h, int d) {
        public int getWrappingArea() {
            return 2 * (this.w * this.h + this.w * this.d + this.h * this.d)
                    + Math.min(Math.min(this.w * this.h, this.w * this.d), this.h * this.d);
        }

        public int getRibbonLength() {
            return 2 * Math.min(Math.min(this.w + this.h, this.w + this.d), this.h + this.d) + this.w * this.h * this.d;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Cuboid> presents;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/2015/day02.txt")))) {
            Pattern p = Pattern.compile("([0-9]+)x([0-9]+)x([0-9]+)");
            presents = br.lines().map(p::matcher).filter(Matcher::matches)
                    .map(m -> new Cuboid(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                            Integer.valueOf(m.group(3))))
                    .toList();
        }
        System.out.println("Part one: " + presents.stream().mapToInt(Cuboid::getWrappingArea).sum());
        System.out.println("Part two: " + presents.stream().mapToInt(Cuboid::getRibbonLength).sum());
    }
}
