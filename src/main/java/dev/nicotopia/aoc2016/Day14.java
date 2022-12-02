package dev.nicotopia.aoc2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
    private static final String input = "jlmsuwbz";
    //private static final String input = "abc";
    private static final List<String> hashes = new LinkedList<>();

    private static String applyAndGetHexString(MessageDigest digest, int idx, boolean stretched) {
        if (idx < hashes.size()) {
            return hashes.get(idx);
        }
        String input = Day14.input + idx;
        String result = null;
        for (int i = 0; i < (stretched ? 2017 : 1); ++i) {
            result = "";
            for (byte b : digest.digest(input.getBytes())) {
                int left = Byte.toUnsignedInt(b) >> 4;
                int right = Byte.toUnsignedInt(b) & 15;
                result += (char) (left < 10 ? '0' + left : 'a' + left - 10);
                result += (char) (right < 10 ? '0' + right : 'a' + right - 10);
            }
            input = result;
        }
        hashes.add(result);
        return result;
    }

    public static void main(String args[]) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        int found = 0;
        Pattern p = Pattern.compile("(\\w)\\1{2}");
        int idx = 0;
        while (found < 64) {
            String hash = applyAndGetHexString(digest, idx, true);
            Matcher m = p.matcher(hash);
            if (m.find()) {
                for (int j = idx + 1; j <= idx + 1000; ++j) {
                    String hash2 = applyAndGetHexString(digest, j, true);
                    if (hash2.indexOf(new String(new char[5]).replace('\0', m.group(1).charAt(0))) != -1) {
                        System.out.printf("%2d: idx %5d - %s\n", found++, idx, hash);
                        break;
                    }
                }
            }
            ++idx;
        }
    }
}