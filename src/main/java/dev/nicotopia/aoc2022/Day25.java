package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day25 {
    public static void main(String[] args) throws IOException {
        List<String> numbers;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day25.class.getResourceAsStream("/2022/day25.txt")))) {
            numbers = br.lines().toList();
        }
        long sum = numbers.stream().mapToLong(n -> {
            long s = 0;
            long factor = 1;
            for (int i = 0; i < n.length(); ++i) {
                s += switch (n.charAt(n.length() - 1 - i)) {
                    case '-' -> -factor;
                    case '=' -> -2 * factor;
                    default -> (n.charAt(n.length() - 1 - i) - '0') * factor;
                };
                factor *= 5;
            }
            System.out.println(n + " -> " + s);
            return s;
        }).sum();
        String s = "";
        while (sum != 0) {
            s = switch ((int) (sum % 5)) {
                case 3 -> '=';
                case 4 -> '-';
                default -> (char) ('0' + sum % 5);
            } + s;
            sum = sum / 5 + (sum % 5 < 3 ? 0 : 1);
        }
        System.out.println("Part one: " + s);
    }
}