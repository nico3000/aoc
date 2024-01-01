package dev.nicotopia.aoc.algebra;

import java.math.BigInteger;

public record Vec4bi(BigInteger x, BigInteger y, BigInteger z, BigInteger w) {
    public Vec4bi(long x, long y, long z, long w) {
        this(BigInteger.valueOf(x), BigInteger.valueOf(y), BigInteger.valueOf(z), BigInteger.valueOf(w));
    }
}
