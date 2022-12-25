package dev.nicotopia;

import java.util.LinkedList;
import java.util.List;

public class Util {
    public static String formatMillis(long millis) {
        long seconds = millis / 1000;
        millis %= 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;
        List<String> out = new LinkedList<>();
        if (days != 0) {
            out.add(String.format("%3d days", days));
        }
        if (days != 0 || hours != 0) {
            out.add(String.format("%2d hours", hours));
        }
        if (days != 0 || hours != 0 || minutes != 0) {
            out.add(String.format("%2d minutes", minutes));
        }
        if (days != 0 || hours != 0 || minutes != 0 || seconds != 0) {
            out.add(String.format("%2d seconds", seconds));
        }
        out.add(String.format("%3d ms", millis));
        return String.join(", ", out);
    }

    public static long gcd(long a, long b) {
        return a == 0 ? b : b == 0 ? a : gcd(Math.max(a, b) % Math.min(a, b), Math.min(a, b));
    }

    public static void main(String[] args) {
        System.out.println(gcd(17 * 2 * 5 * 13, 17 * 2 * 7 * 11));
    }
}
