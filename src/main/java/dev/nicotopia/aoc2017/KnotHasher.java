package dev.nicotopia.aoc2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class KnotHasher {
    private int current = 0;
    private int skip = 0;
    private byte data[] = new byte[256];

    public KnotHasher() {
        this.reset();
    }

    public void reset() {
        this.current = 0;
        this.skip = 0;
        IntStream.range(0, 256).forEach(i -> this.data[i] = (byte) i);
    }

    public byte[] hash(byte source[]) {
        this.reset();
        List<Integer> lengths = new ArrayList<>(
                IntStream.range(0, source.length).mapToObj(i -> Byte.toUnsignedInt(source[i])).toList());
        lengths.addAll(Arrays.asList(17, 31, 73, 47, 23));
        for (int i = 0; i < 64; ++i) {
            this.hashOneRound(lengths);
        }
        byte hashed[] = new byte[16];
        for (int i = 0; i < 16; ++i) {
            hashed[i] = this.data[16 * i];
            for (int j = 1; j < 16; ++j) {
                hashed[i] ^= this.data[16 * i + j];
            }
        }
        return hashed;
    }

    public byte[] hashOneRound(List<Integer> lengths) {
        for (int b : lengths) {
            for (int i = 0; i < b / 2; ++i) {
                byte t = this.data[(this.current + i) % this.data.length];
                this.data[(this.current + i) % this.data.length] = this.data[(this.current + b - 1 - i)
                        % this.data.length];
                this.data[(this.current + b - 1 - i) % this.data.length] = t;
            }
            this.current += b + this.skip++;
        }
        return this.data;
    }

    public String toHexString(byte data[]) {
        return IntStream.range(0, data.length).map(i -> Byte.toUnsignedInt(data[i]))
                .collect(StringBuilder::new, (sb, v) -> sb.append(String.format("%02x", v)), StringBuilder::append)
                .toString();
    }
}
