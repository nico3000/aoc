package dev.nicotopia.aoc2016;

public class Day18 {
    public static void main(String[] args) {
        String lastRow = "^..^^.^^^..^^.^...^^^^^....^.^..^^^.^.^.^^...^.^.^.^.^^.....^.^^.^.^.^.^.^.^^..^^^^^...^.....^....^.";
        int safeCount = (int) lastRow.chars().filter(c -> c == '.').count();
        for (int i = 1; i < 400000; ++i) {
            String row = "";
            for (int j = 0; j < lastRow.length(); ++j) {
                boolean left = j != 0 && lastRow.charAt(j - 1) == '^';
                boolean right = j != lastRow.length() - 1 && lastRow.charAt(j + 1) == '^';
                boolean tile = left ^ right;
                row += tile ? '^' : '.';
                safeCount += tile ? 0 : 1;
            }
            lastRow = row;
        }
        System.out.println("Safe tile count: " + safeCount);
    }
}