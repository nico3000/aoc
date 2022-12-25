package dev.nicotopia;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class Hasher {
    public static String md5(String source) throws NoSuchAlgorithmException {
        byte digest[] = MessageDigest.getInstance("MD5").digest(source.getBytes());
        return IntStream.range(0, digest.length).collect(StringBuilder::new,
                (sb, i) -> sb.append(String.format("%02x", digest[i])), StringBuilder::append).toString();
    }
}
