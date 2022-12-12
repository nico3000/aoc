package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

public class Day07 {
    private static record File(String name, long size) {
    }

    private static class Directory {
        private Directory parent;
        private final Map<String, Directory> directories = new HashMap<>();
        private final Map<String, File> files = new HashMap<>();

        public Directory(Directory parent) {
            this.parent = parent;
        }

        public Directory getRoot() {
            return this.parent == null ? this : this.parent.getRoot();
        }

        public long getSize() {
            return this.directories.values().stream().mapToLong(Directory::getSize).sum()
                    + this.files.values().stream().mapToLong(File::size).sum();
        }

        public void depthSearch(Consumer<Directory> visitor) {
            this.directories.values().forEach(d -> d.depthSearch(visitor));
            visitor.accept(this);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day07.class.getResourceAsStream("/2022/day07.txt")))) {
            lines = br.lines().toList();
        }
        Directory current = new Directory(null);
        for (String line : lines) {
            if (line.equals("$ cd /")) {
            } else if (line.startsWith("$ cd")) {
                String dirName = line.substring(4).trim();
                current = dirName.equals("..") ? current.parent : current.directories.get(dirName);
            } else if (line.startsWith("dir")) {
                current.directories.put(line.substring(3).trim(), new Directory(current));
            } else if (line.charAt(0) != '$') {
                String split[] = line.split("\\s+");
                current.files.put(split[1].trim(), new File(split[1].trim(), Long.valueOf(split[0].trim())));
            }
        }
        current = current.getRoot();
        long capacity = 70000000;
        long targetFreeSize = 30000000;
        long freeSize = capacity - current.getSize();
        LongAccumulator minAccu = new LongAccumulator((l, r) -> l < r ? l : r, Long.MAX_VALUE);
        LongAdder adder = new LongAdder();
        current.depthSearch(d -> {
            long size = d.getSize();
            if (size <= 100000L) {
                adder.add(size);
            }
            if (targetFreeSize - freeSize <= size) {
                minAccu.accumulate(size);
            }
        });
        System.out.printf("Part one: %d, part two: %d\n", adder.sum(), minAccu.get());
    }
}