package dev.nicotopia.aoc2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day05 {
    public static void main(String args[]) throws NoSuchAlgorithmException {
        String input = "abbhdwsy";
        int idx = 0;
        int length1 = 0;
        int length2 = 0;
        char password[] = new char[8];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        while (length1 != 8 || length2 != 8) {
            byte hash[] = md5.digest((input + idx++).getBytes());
            if (hash[0] == 0 && hash[1] == 0 && 0 <= hash[2] && hash[2] < 16) {
                if(length1 != 8) {
                    System.out.print((char) (hash[2] < 10 ? '0' + hash[2] : 'a' + hash[2] - 10));
                    ++length1;
                }
                int pos = hash[2] & 15;
                int c = Byte.toUnsignedInt(hash[3]) >> 4;
                if(pos < 8 && password[pos] == 0) {
                    password[pos] = (char) (c < 10 ? '0' + c : 'a' + c - 10);
                    ++length2;
                }
            }
        }
        System.out.println("\n" + new String(password));
    }
}
