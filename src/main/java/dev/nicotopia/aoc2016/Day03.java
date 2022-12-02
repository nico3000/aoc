package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Day03 {
    private record Triplet(int x, int y, int z) {
        public boolean isInvalid() {
            return this.x + this.y <= this.z || this.x + this.z <= this.y || this.y + this.z <= this.x;
        }
    }

    public static void main(String args[]) throws IOException {
        List<Integer> raw = new LinkedList<>();
        List<Triplet> rowTriplets = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day03.class.getResourceAsStream("/2016/day03.txt")))) {
            br.lines().forEach(line -> {
                String split[] = line.trim().split("\\s+");
                raw.add(Integer.valueOf(split[0]));
                raw.add(Integer.valueOf(split[1]));
                raw.add(Integer.valueOf(split[2]));
                rowTriplets.add(new Triplet(raw.get(raw.size() - 3), raw.get(raw.size() - 2), raw.get(raw.size() - 1)));
            });
        }
        List<Triplet> colTriplets = new LinkedList<>();
        for (int i = 0; i < raw.size(); i += 9) {
            colTriplets.add(new Triplet(raw.get(i), raw.get(i + 3), raw.get(i + 6)));
            colTriplets.add(new Triplet(raw.get(i + 1), raw.get(i + 4), raw.get(i + 7)));
            colTriplets.add(new Triplet(raw.get(i + 2), raw.get(i + 5), raw.get(i + 8)));
        }
        rowTriplets.removeIf(Triplet::isInvalid);
        colTriplets.removeIf(Triplet::isInvalid);
        System.out.printf("Row triangles: %d, col triangles: %d\n", rowTriplets.size(), colTriplets.size());
    }
}
