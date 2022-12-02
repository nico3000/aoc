package dev.nicotopia.aoc2021.day24;

import java.util.Arrays;

public class Day24_2 {
    private static final int a[] = new int[] { 1, 1, 1, 26, 1, 26, 1, 1, 1, 26, 26, 26, 26, 26 };
    private static final int b[] = new int[] { 10, 13, 13, -11, 11, -4, 12, 12, 15, -2, -5, -11, -13, -10 };
    private static final int c[] = new int[] { 13, 10, 3, 1, 9, 3, 5, 1, 0, 13, 7, 15, 12, 8 };

    public static void main(String args[]) {
        int threadCount = 32;
        for (int t = 0; t < threadCount; ++t) {
            final int idx = t;
            new Thread(() -> {
                int model[] = new int[14];
                Arrays.fill(model, 1);
                for (int i = 0; i < idx; ++i) {
                    addOne(model, model.length - 1);
                }
                long last = System.currentTimeMillis();
                boolean done = false;
                while (!done) {
                    int z = 0;
                    for (int i = 0; i < model.length; ++i) {
                        int x = z % 26 + b[i] == model[i] ? 0 : 1;
                        z = z / a[i] * (25 * x + 1) + (model[i] + c[i]) * x;
                    }
                    //print(model, z);
                    if (z == 0 || 120000 < System.currentTimeMillis() - last) {
                        synchronized (Day24_2.class) {
                            System.out.printf("Thread %2d, ", idx);
                            print(model, z);
                        }
                        last = System.currentTimeMillis();
                    }
                    for (int i = 0; !done && i < threadCount; ++i) {
                        done |= !addOne(model, model.length - 1);
                    }
                }
            }).start();
        }
    }

    public static boolean subOne(int model[], int pos) {
        if (pos == 0 && model[pos] == 1) {
            return false;
        }
        if (model[pos] == 1) {
            model[pos] = 9;
            return subOne(model, pos - 1);
        } else {
            --model[pos];
            return true;
        }
    }

    public static boolean addOne(int model[], int pos) {
        if (pos == 0 && model[pos] == 9) {
            return false;
        }
        if (model[pos] == 9) {
            model[pos] = 1;
            return addOne(model, pos - 1);
        } else {
            ++model[pos];
            return true;
        }
    }

    public static void print(int model[], int z) {
        for (int i : model) {
            System.out.print(i);
        }
        System.out.println(": " + z);
    }
}
