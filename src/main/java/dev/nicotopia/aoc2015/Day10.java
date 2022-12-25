package dev.nicotopia.aoc2015;

public class Day10 {
    public static void main(String[] args) {
        String input = "3113322113";
        int partOne = 0;
        for (int i = 0; i < 50; ++i) {
            StringBuilder newInput = new StringBuilder();
            int pos = 0;
            while (pos != input.length()) {
                int count = 1;
                while (++pos != input.length() && input.charAt(pos) == input.charAt(pos - 1)) {
                    ++count;
                }
                newInput.append(count + "" + (char) input.charAt(pos - 1));
            }
            input = newInput.toString();
            if (i + 1 == 40) {
                partOne = input.length();
            }
        }
        System.out.printf("Part one: %d\nPart two: %d\n", partOne, input.length());
    }
}