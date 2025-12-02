package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day09 extends DayBase {
    private record File(int from, int to, int id) {
        public int getSize() {
            return this.to - this.from;
        }
    }

    private long getChecksum(int disk[]) {
        return IntStream.range(0, disk.length).filter(i -> disk[i] != -1).mapToLong(i -> i * disk[i]).sum();
    }

    private long partOne(int disk[]) {
        int r = disk.length - 1;
        for (int l = 0; l < r; ++l) {
            if (disk[l] == -1) {
                while (l < r && disk[r] == -1) {
                    --r;
                }
                if (l == r) {
                    break;
                }
                disk[l] = disk[r];
                disk[r] = -1;
            }
        }
        return this.getChecksum(disk);
    }

    private List<File> extractFiles(int diskMap[]) {
        List<File> files = new ArrayList<>();
        int p = 0;
        for (int i = 0; i < diskMap.length; ++i) {
            if (i % 2 == 0) {
                files.addFirst(new File(p, p + diskMap[i], i / 2));
            }
            p += diskMap[i];
        }
        return files;
    }

    private void tryMoveFile(File file, int disk[]) {
        for (int i = 0; i < file.from();) {
            int numSameBlocks = 1;
            while (i + numSameBlocks < disk.length && disk[i + numSameBlocks] == disk[i]) {
                ++numSameBlocks;
            }
            if (disk[i] == -1 && file.getSize() <= numSameBlocks) {
                for (int j = 0; j < file.getSize(); ++j) {
                    disk[i + j] = file.id();
                    disk[file.from() + j] = -1;
                }
                break;
            }
            i += numSameBlocks;
        }
    }

    private long partTwo(int diskMap[], int disk[]) {
        this.extractFiles(diskMap).stream().forEach(f -> this.tryMoveFile(f, disk));
        return this.getChecksum(disk);
    }

    @Override
    public void run() {
        int diskMap[] = this.getPrimaryPuzzleInput().getFirst().chars().map(i -> i - '0').toArray();
        int disk[] = new int[Arrays.stream(diskMap).sum()];
        int p = 0;
        for (int i = 0; i < diskMap.length; ++i) {
            for (int j = 0; j < diskMap[i]; ++j) {
                disk[p++] = i % 2 == 0 ? i / 2 : -1;
            }
        }

        this.addTask("Part one", () -> this.partOne(Arrays.copyOf(disk, disk.length)));
        this.addTask("Part two", () -> this.partTwo(diskMap, Arrays.copyOf(disk, disk.length)));
    }
}
