package webserver.util;

public class ByteUtils {
    public static int unsignedValue(byte b) {
        return b & 0xFF;
    }

    public static boolean isBitSet(byte b, int bit) {
        return (b & (1 << (7 - bit))) != 0;
    }

    public static byte setBit(byte b, int bit) {
        return (byte) (b | (1 << (7 - bit)));
    }

    // https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
