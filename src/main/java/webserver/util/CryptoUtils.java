package webserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {
    public static MessageDigest SHA1;
    static {
        try {
            SHA1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static byte[] hash(MessageDigest digest, String... strings) {
        digest.reset();
        for (String string : strings) {
            digest.update(string.getBytes());
        }
        return digest.digest();
    }
}
