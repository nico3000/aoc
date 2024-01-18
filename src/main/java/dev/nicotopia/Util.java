package dev.nicotopia;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.LongPredicate;
import java.util.stream.Stream;

public class Util {
    public static String formatMillis(long millis) {
        return formatNanos(millis * 1000000);
    }

    public static String formatNanos(long nanos) {
        if (nanos == 0) {
            return "0 ms";
        }
        if (nanos < 1000000000 && nanos % 1000000 != 0) {
            return String.format("%.3f ms", 1e-6f * (float) nanos);
        }
        long micros = nanos / 1000;
        nanos %= nanos;
        long millis = micros / 1000;
        micros %= 1000;
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
        if (days != 0 || hours != 0 || minutes != 0 || seconds != 0 || millis != 0) {
            out.add(String.format("%2d ms", millis));
        }
        return String.join(", ", out);
    }

    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return a == 0 ? b : b == 0 ? a : gcd(Math.max(a, b) % Math.min(a, b), Math.min(a, b));
    }

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return a == 0 ? b : b == 0 ? a : gcd(Math.max(a, b) % Math.min(a, b), Math.min(a, b));
    }

    public static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    public static <K, V> Stream<Pair<K, V>> pairStream(Map<K, V> map) {
        return map.keySet().stream().map(k -> new Pair<>(k, map.get(k)));
    }

    public static long binomial(int n, int k) {
        long c = 1;
        if (k > n - k) {
            k = n - k;
        }
        for (int i = 1; i <= k; i++, n--) {
            if (c / i > Long.MAX_VALUE / n) {
                return 0;
            }
            c = c / i * n + c % i * n / i;
        }
        return c;
    }

    public static int largestOf(int a, int... rem) {
        int m = a;
        for (int v : rem) {
            m = Math.max(m, v);
        }
        return m;
    }

    public static int lowestOf(int a, int... rem) {
        int m = a;
        for (int v : rem) {
            m = Math.min(m, v);
        }
        return m;
    }

    public static float largestOf(float a, float... rem) {
        float m = a;
        for (float v : rem) {
            m = Math.max(m, v);
        }
        return m;
    }

    public static float lowestOf(float a, float... rem) {
        float m = a;
        for (float v : rem) {
            m = Math.min(m, v);
        }
        return m;
    }

    public static int clamp(int v, int min, int max) {
        return Math.min(Math.max(min, v), max);
    }

    public static double clamp(double v, double min, double max) {
        return Math.min(Math.max(min, v), max);
    }

    /**
     * Returns the smallest v with less(v) == false
     * @param less A function
     * @return The smallest v with less(v) == false
     */
    public static long binarySearch(LongPredicate less) {
        long r = 1;
        while (less.test(r)) {
            r *= 2;
        }
        long l = r / 2;
        while (l < r) {
            long m = (l + r) / 2;
            if (less.test(m)) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l;
    }
}
