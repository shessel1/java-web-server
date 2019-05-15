package webserver.ws;

import webserver.util.BitComparable;

public enum WSOpcode implements BitComparable {
    CONT(0x0),
    TEXT(0x1),
    BIN(0x2),
    CLOSE(0x8),
    PING(0x9),
    PONG(0xA);

    private byte bitPattern;

    WSOpcode(int bitPattern) {
        this.bitPattern = (byte) bitPattern;
    }

    @Override
    public byte getBitPattern() {
        return bitPattern;
    }

    public static WSOpcode fromByte(byte value) throws WSException {
        byte code = (byte) (value & 0b00001111);
        for (WSOpcode opcode : WSOpcode.values()) {
            if (code == opcode.getBitPattern()) {
                return opcode;
            }
            /*if (opcode.sameBitsSet(value)) {
                return opcode;
            }*/
        }
        throw new WSException(String.format("Unsupported opcode \"%02X\"", code));
    }
}
