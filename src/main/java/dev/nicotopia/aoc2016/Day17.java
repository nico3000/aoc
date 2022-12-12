package dev.nicotopia.aoc2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class Day17 {
    private static String passcode = "awrkjxxr";
    private static String shortestPath = null;
    private static int longestPath = 0;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        find("", 0, 0);
        System.out.println("Shortest path: " + shortestPath);
        System.out.println("Longest path: " + longestPath);
    }

    private static void find(String path, int x, int y) {
        if (x == 3 && y == 3) {
            if (shortestPath == null || path.length() < shortestPath.length()) {
                shortestPath = path;
            }
            if (longestPath < path.length()) {
                longestPath = path.length();
            }
        } else {
            String md5 = md5(passcode + path);
            if (y != 0 && 'a' < md5.charAt(0)) {
                find(path + 'U', x, y - 1);
            }
            if (y != 3 && 'a' < md5.charAt(1)) {
                find(path + 'D', x, y + 1);
            }
            if (x != 0 && 'a' < md5.charAt(2)) {
                find(path + 'L', x - 1, y);
            }
            if (x != 3 && 'a' < md5.charAt(3)) {
                find(path + 'R', x + 1, y);
            }
        }
    }

    private static String md5(String source) {
        try {
            byte digest[] = MessageDigest.getInstance("MD5").digest(source.getBytes());
            return IntStream.range(0, digest.length).collect(StringBuilder::new,
                    (sb, i) -> sb.append(String.format("%2x", digest[i])), StringBuilder::append).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}