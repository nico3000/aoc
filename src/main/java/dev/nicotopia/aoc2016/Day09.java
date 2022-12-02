package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day09 {
    public static void main(String args[]) throws IOException {
        String compressed;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day09.class.getResourceAsStream("/2016/day09.txt")))) {
            compressed = br.readLine();
        }
        System.out.println("non-recursive decompressed length: " + getDecompressedLength(compressed, false));
        System.out.println("    recursive decompressed length: " + getDecompressedLength(compressed, true));
    }

    private static long getDecompressedLength(String block, boolean recursive) {
        Matcher m = Pattern.compile("\\((\\d+)x(\\d+)\\)").matcher(block);
        int pos = 0;
        long length = 0L;
        while (m.find(pos)) {
            length += m.start() - pos;
            int charCount = Integer.valueOf(m.group(1));
            int repeatCount = Integer.valueOf(m.group(2));
            String toRepeat = block.substring(m.end(), pos = m.end() + charCount);
            length += repeatCount * (recursive ? getDecompressedLength(toRepeat, true) : toRepeat.length());
        }
        return length + block.length() - pos;
    }
}