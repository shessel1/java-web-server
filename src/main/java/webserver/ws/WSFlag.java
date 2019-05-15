package webserver.ws;

import webserver.util.BitComparable;

import java.util.HashSet;
import java.util.Set;

public enum WSFlag implements BitComparable {
    FIN((byte) 0b10000000),
    RSV1((byte) 0b01000000),
    RSV2((byte) 0b00100000),
    RSV3((byte) 0b00010000);

    private byte bitPattern;

    WSFlag(byte bitPattern) {
        this.bitPattern = bitPattern;
    }

    @Override
    public byte getBitPattern() {
        return bitPattern;
    }

    public static byte toByte(Set<WSFlag> flags) {
        byte result = 0;
        for (WSFlag flag : flags) {
            result |= flag.getBitPattern();
        }
        return result;
    }

    public static Set<WSFlag> fromByte(byte value) {
        Set<WSFlag> flags = new HashSet<>();
        for (WSFlag flag : WSFlag.values()) {
            if (flag.sameBitsSet(value)) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
