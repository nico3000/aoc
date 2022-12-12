package dev.nicotopia.aoc2016;

public class Day16 {
    public static void main(String[] args) {
        String input = "11110010111001001";
        int diskLength = 35651584;
        while (input.length() < diskLength) {
            input += '0' + new StringBuilder(input).reverse().chars()
                    .mapToObj(c -> Character.valueOf((char) ('0' + '1' - c)))
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        }
        input = input.substring(0, diskLength);
        while (input.length() % 2 == 0) {
            StringBuilder newInput = new StringBuilder();
            for (int i = 0; i < input.length(); i += 2) {
                newInput.append(input.charAt(i) == input.charAt(i + 1) ? '1' : '0');
            }
            input = newInput.toString();
        }
        System.out.println("Checksum: " + input);
    }
}