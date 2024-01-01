package dev.nicotopia.aoc.algebra;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BiFunction;

public class Mat4x4bi {
    private BigInteger values[][] = { { BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO },
            { BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO },
            { BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO },
            { BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE } };

    public void set(int r, int c, BigInteger v) {
        this.values[r][c] = v;
    }

    public void set(int r, int c, long v) {
        this.values[r][c] = BigInteger.valueOf(v);
    }

    public void setRow(int r, BigInteger v0, BigInteger v1, BigInteger v2, BigInteger v3) {
        this.values[r][0] = v0;
        this.values[r][1] = v1;
        this.values[r][2] = v2;
        this.values[r][3] = v3;
    }

    public void setRow(int r, long v0, long v1, long v2, long v3) {
        this.values[r][0] = BigInteger.valueOf(v0);
        this.values[r][1] = BigInteger.valueOf(v1);
        this.values[r][2] = BigInteger.valueOf(v2);
        this.values[r][3] = BigInteger.valueOf(v3);
    }

    public BigInteger det() {
        BigInteger a = this.get(0, 0).multiply(this.det3x3(0, 0));
        BigInteger b = this.get(0, 1).multiply(this.det3x3(0, 1)).negate();
        BigInteger c = this.get(0, 2).multiply(this.det3x3(0, 2));
        BigInteger d = this.get(0, 3).multiply(this.det3x3(0, 3)).negate();
        return a.add(b).add(c).add(d);
    }

    public BigInteger get(int r, int c) {
        return this.values[r][c];
    }

    private BigInteger det3x3(int sr, int sc) {
        BiFunction<Integer, Integer, BigInteger> v = (r, c) -> this.values[r < sr ? r : r + 1][c < sc ? c : c + 1];
        BigInteger a = v.apply(0, 0).multiply(v.apply(1, 1)).multiply(v.apply(2, 2));
        BigInteger b = v.apply(1, 0).multiply(v.apply(2, 1)).multiply(v.apply(0, 2));
        BigInteger c = v.apply(2, 0).multiply(v.apply(0, 1)).multiply(v.apply(1, 2));
        BigInteger d = v.apply(0, 2).multiply(v.apply(1, 1)).multiply(v.apply(2, 0)).negate();
        BigInteger e = v.apply(0, 0).multiply(v.apply(1, 2)).multiply(v.apply(2, 1)).negate();
        BigInteger f = v.apply(0, 1).multiply(v.apply(1, 0)).multiply(v.apply(2, 2)).negate();
        return a.add(b).add(c).add(d).add(e).add(f);
    }

    public Mat4x4bi adjugate() {
        Mat4x4bi a = new Mat4x4bi();
        for (int r = 0; r < 4; ++r) {
            for (int c = 0; c < 4; ++c) {
                a.set(c, r, this.det3x3(r, c).multiply(BigInteger.valueOf((r + c) % 2 == 0 ? 1 : -1)));
            }
        }
        return a;
    }

    public Mat4x4bi mul(Mat4x4bi right) {
        Mat4x4bi result = new Mat4x4bi();
        for (int r = 0; r < 4; ++r) {
            for (int c = 0; c < 4; ++c) {
                BigInteger v = BigInteger.ZERO;
                for (int i = 0; i < 4; ++i) {
                    v = v.add(this.get(r, i).multiply(right.get(i, c)));
                }
                result.set(r, c, v);
            }
        }
        return result;
    }

    public Vec4bi mul(Vec4bi v) {
        BigInteger x = this.get(0, 0).multiply(v.x()).add(this.get(0, 1).multiply(v.y()))
                .add(this.get(0, 2).multiply(v.z())).add(this.get(0, 3).multiply(v.w()));
        BigInteger y = this.get(1, 0).multiply(v.x()).add(this.get(1, 1).multiply(v.y()))
                .add(this.get(1, 2).multiply(v.z())).add(this.get(1, 3).multiply(v.w()));
        BigInteger z = this.get(2, 0).multiply(v.x()).add(this.get(2, 1).multiply(v.y()))
                .add(this.get(2, 2).multiply(v.z())).add(this.get(2, 3).multiply(v.w()));
        BigInteger w = this.get(3, 0).multiply(v.x()).add(this.get(3, 1).multiply(v.y()))
                .add(this.get(3, 2).multiply(v.z())).add(this.get(3, 3).multiply(v.w()));
        return new Vec4bi(x, y, z, w);
    }

    @Override
    public String toString() {
        int maxLength = Arrays.stream(this.values).map(Arrays::stream).flatMap(s -> s)
                .mapToInt(v -> v.toString().length()).max().getAsInt();
        String s = "";
        for (int r = 0; r < 4; ++r) {
            for (int c = 0; c < 4; ++c) {
                String v = this.get(r, c).toString();
                s += String.format((c == 0 ? "" : " ") + "%" + maxLength + "s", v);
            }
            s += "\n";
        }
        return s.stripTrailing();
    }
}
