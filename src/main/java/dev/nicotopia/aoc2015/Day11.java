package dev.nicotopia.aoc2015;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Day11 {
    public static void main(String[] args) {
        String input = "hxbxwxba";
        String nextPassword = findNextPassword(input);
        String nextNextPassword = findNextPassword(nextPassword);
        System.out.printf("Part one: %s\nPart two: %s\n", nextPassword, nextNextPassword);
    }

    private static String findNextPassword(String current) {
        char password[] = current.toCharArray();
        do {
            for (int i = 0; i < password.length; ++i) {
                if (password[password.length - 1 - i] == 'z') {
                    password[password.length - 1 - i] = 'a';
                } else {
                    ++password[password.length - 1 - i];
                    break;
                }
            }
        } while (!isValid(new String(password)));
        return new String(password);
    }

    private static boolean isValid(String password) {
        boolean condA = IntStream.range('a', 'x' + 1)
                .mapToObj(i -> new String(new char[] { (char) i, (char) (i + 1), (char) (i + 2) }))
                .anyMatch(str -> password.contains(str));
        boolean condB = Arrays.asList('i', 'o', 'l').stream().noneMatch(c -> password.indexOf(c) != -1);
        boolean condC = 2 <= IntStream.range('a', 'z' + 1).mapToObj(c -> new String(new char[] { (char) c, (char) c }))
                .filter(s -> password.contains(s)).count();
        return condA && condB && condC;
    }
}