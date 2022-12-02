package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Day03 {
    public static void main(String[] args) throws IOException {
        List<String> values = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day03.class.getResourceAsStream("/2021/day03.txt")))) {
            br.lines().forEach(values::add);
        }
        List<String> valuesO2 = new LinkedList<>(values);
        List<String> valuesCO2 = new LinkedList<>(values);
        int gamma = 0;
        for (int c = 0; c < values.get(0).length(); ++c) {
            gamma = 2 * gamma + (mcb(values, c));
            char mcb = (char)('0' + mcb(valuesO2, c));
            char lcb = (char)('1' - mcb(valuesCO2, c));
            int finalCol = c;
            valuesO2.removeIf(v -> valuesO2.size() != 1 && v.charAt(finalCol) != mcb);
            valuesCO2.removeIf(v -> valuesCO2.size() != 1 && v.charAt(finalCol) != lcb);
        }
        int epsilon = (1 << values.get(0).length()) - 1 - gamma;
        System.out.printf("gamma=%d, epsilon=%d, power=%d\n", gamma, epsilon, gamma * epsilon);
        
        int o2 = Integer.parseInt(valuesO2.get(0), 2);
        int co2 = Integer.parseInt(valuesCO2.get(0), 2);
        System.out.printf("o2=%d, co2=%d, o2*co2=%d\n", o2, co2, o2 * co2);
    }

    public static int mcb(List<String> values, int pos) {
        int zeros = 0;
        int ones = 0;
        for (String value : values) {
            switch (value.charAt(pos)) {
                case '0' -> ++zeros;
                case '1' -> ++ones;
            }
        }
        return zeros <= ones ? 1 : 0;
    }
}