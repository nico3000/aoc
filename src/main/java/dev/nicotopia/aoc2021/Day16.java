package dev.nicotopia.aoc2021;

import java.io.IOException;
import java.io.InputStream;

public class Day16 {
    private static class BinaryStream {
        private final long[] bytes;
        private int pos = 0;

        public BinaryStream(byte asciiBytes[]) {
            this.bytes = new long[(asciiBytes.length + 15) / 16];
            for (int i = 0; i < this.bytes.length; ++i) {
                this.bytes[i] = 0;
                for (int j = 0; j < 16 && 16 * i + j < asciiBytes.length; ++j) {
                    byte c = asciiBytes[16 * i + j];
                    long hex = '0' <= c && c <= '9' ? c - '0' : c + 10 - 'A';
                    this.bytes[i] |= hex << (60 - j * 4);
                }
            }
        }

        public int getReadBitCount() {
            return this.pos;
        }

        public long read(int bitCount) throws IOException {
            assert (bitCount <= 64);
            int firstBits = Math.min(bitCount, 64 - this.pos % 64);
            long result = this.bytes[this.pos / 64] >> (64 - this.pos % 64 - firstBits) & ~(-1 << firstBits);
            bitCount -= firstBits;
            this.pos += firstBits;
            if (bitCount != 0) {
                result = result << bitCount | this.bytes[this.pos / 64] >>> (64 - bitCount);
                this.pos += bitCount;
            }
            return result;
        }
    }

    private static int versionSum = 0;

    public static void main(String args[]) throws IOException {
        byte bytes[];
        try (InputStream is = Day16.class.getResourceAsStream("/2021/day16.txt")) {
            bytes = is.readAllBytes();
        }
        long begin = System.nanoTime();
        long result = readPacket(new BinaryStream(bytes));
        long end = System.nanoTime();
        System.out.printf("version sum: %d, result: %d, elapsed time: %.3f ms\n", versionSum, result,
                1e-6f * (float) (end - begin));
    }

    public static long readPacket(BinaryStream bs) throws IOException {
        versionSum += bs.read(3);
        long typeId = bs.read(3);
        return typeId == 4 ? readLiteralValue(bs) : readOperator(bs, (int) typeId);
    }

    public static long readLiteralValue(BinaryStream bs) throws IOException {
        long v = 0;
        long group;
        do {
            group = bs.read(5);
            v = (v << 4) | (group & 15);
        } while ((group >> 4) == 1);
        return v;
    }

    public static long readOperator(BinaryStream bs, int typeId) throws IOException {
        long type = bs.read(1);
        long len = bs.read(type == 0 ? 15 : 11);
        int begin = bs.getReadBitCount();
        long accu = readPacket(bs);
        if (typeId < 5) {
            int i = 1;
            while ((type == 0 && bs.getReadBitCount() - begin != len) || (type == 1 && i++ < len)) {
                accu = combinePackets(typeId, accu, readPacket(bs));
            }
            return accu;
        } else {
            return combinePackets(typeId, accu, readPacket(bs));
        }
    }

    public static long combinePackets(int typeId, long left, long right) {
        return switch (typeId) {
            case 0 -> left + right;
            case 1 -> left * right;
            case 2 -> Long.min(left, right);
            case 3 -> Long.max(left, right);
            case 5 -> right < left ? 1L : 0L;
            case 6 -> left < right ? 1L : 0L;
            case 7 -> left == right ? 1L : 0L;
            default -> throw new RuntimeException();
        };
    }
}