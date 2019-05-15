package webserver.util;

public interface BitComparable {
    byte getBitPattern();

    default boolean sameBitsSet(Byte other) {
        byte bitPattern = getBitPattern();
        return ((getBitPattern() & other) == bitPattern);
    }
}
