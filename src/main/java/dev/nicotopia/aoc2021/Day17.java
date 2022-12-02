package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {
    private static record Area(int fromX, int toX, int fromY, int toY) {
        public boolean isInside(int x, int y) {
            return this.fromX <= x && x <= this.toX && this.fromY <= y && y <= this.toY;
        }
    }

    public static void main(String args[]) throws IOException {
        Area targetArea;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day17.class.getResourceAsStream("/2021/day17.txt")))) {
            Matcher m = Pattern.compile("^target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)$")
                    .matcher(br.readLine());
            if (m.matches()) {
                targetArea = new Area(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                        Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)));
            } else {
                throw new RuntimeException();
            }
        }
        long begin = System.nanoTime();
        int vy = -targetArea.fromY - 1;
        int yMax = vy * (vy + 1) / 2;
        int vxMin = (int) Math.ceil(-0.5 + Math.sqrt(0.25 + (double) (2 * targetArea.fromX)));
        int hitCount = checkVelocities(targetArea, vxMin, targetArea.toX, targetArea.fromY, vy);
        long end = System.nanoTime();
        System.out.printf("y_max=%d, hit count: %d, time: %.3f ms.\n", yMax, hitCount, 1e-6f * (float) (end - begin));
    }

    public static int checkVelocities(Area targetArea, int vxMin, int vxMax, int vyMin, int vyMax) {
        int count = 0;
        for (int vy0 = vyMin; vy0 <= vyMax; ++vy0) {
            for (int vx0 = vxMin; vx0 <= vxMax; ++vx0) {
                int x = 0, y = 0;
                int vx = vx0, vy = vy0;
                boolean hit = targetArea.isInside(x, y);
                while (!hit && x <= targetArea.toX && targetArea.fromY <= y) {
                    hit = targetArea.isInside(x += vx, y += vy--);
                    vx = vx != 0 ? vx - 1 : 0;
                }
                count += hit ? 1 : 0;
            }
        }
        return count;
    }
}