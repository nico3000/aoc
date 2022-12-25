package dev.nicotopia.aoc2015;

import java.security.NoSuchAlgorithmException;

import dev.nicotopia.Hasher;

public class Day04 {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String input = "iwrupvqb";
        Integer idx1 = null;
        int idx2;
        for (int i = 0; true; ++i) {
            String hash = Hasher.md5(input + i);
            if (hash.startsWith("00000")) {
                if (idx1 == null) {
                    idx1 = i;
                }
                if (hash.charAt(5) == '0') {
                    idx2 = i;
                    break;
                }
            }
        }
        System.out.printf("Part one: %d, part two: %d\n", idx1, idx2);
    }
}
