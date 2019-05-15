package webserver.ws;

import webserver.util.ArrayUtils;

import java.util.*;
import java.util.List;

import static webserver.util.ByteUtils.isBitSet;

public class WSFrame {
    public static final WSFrame PING = new WSFrame(WSOpcode.PING, WSFlag.FIN);

    private WSOpcode opcode;
    private Set<WSFlag> flags;
    private byte[] data;
    private WSFrame next;

    public WSFrame(WSOpcode opcode, WSFlag flag) {
        this(opcode, flag, new byte[0]);
    }

    public WSFrame(WSOpcode opcode, WSFlag flag, byte[] data) {
        this(opcode, Set.of(flag), data);
    }

    public WSFrame(WSOpcode opcode, Set<WSFlag> flags, byte[] data) {
        this.opcode = opcode;
        this.flags = flags;
        this.data = data;
    }

    public WSOpcode getOpcode() {
        return opcode;
    }

    public boolean hasFlag(WSFlag flag) {
        return flags.contains(flag);
    }

    public Set<WSFlag> getFlags() {
        return flags;
    }

    public void setFlag(WSFlag flag) {
        flags.add(flag);
    }

    public void unsetFlag(WSFlag flag) {
        flags.remove(flag);
    }

    public byte[] getData() {
        return data;
    }

    public boolean isLastFrame() {
        return hasFlag(WSFlag.FIN) && next == null;
    }

    public void setNextFrame(WSFrame frame) throws WSException {
        if (this.hasFlag(WSFlag.FIN)) {
            throw new WSException("Cannot add to the final frame");
        }
        if (frame.getOpcode() != WSOpcode.CONT) {
            throw new WSException("Frame is not a continuation frame");
        }
        this.next = frame;
    }

    public static WSFrame decode(byte[] encoded) throws WSException {
        int offset = 2;
        switch ((byte) (encoded[1] & 127)) {
            case 126: offset += 2; break;
            case 127: offset += 8; break;
        }
        byte[] data;
        try {
            if (isBitSet(encoded[1], 0))
            {
                byte[] mask = Arrays.copyOfRange(encoded, offset, offset += 4);
                data = new byte[encoded.length - offset];
                for (int i = offset, j = 0; i < encoded.length; i++, j++) {
                    data[j] = (byte) (encoded[i] ^ mask[j % 4]);
                }
            } else {
                data = Arrays.copyOfRange(encoded, offset, encoded.length);
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new WSException("Unexpected end of frame", ex);
        } catch (Exception ex) {
            throw new WSException("Failed to decode frame", ex);
        }
        return new WSFrame(WSOpcode.fromByte(encoded[0]), WSFlag.fromByte(encoded[0]), data);
    }

    public byte[] encode() {
        List<Byte> encoded = new ArrayList<>();
        int offset = 2;
        encoded.add(0, (byte) (WSFlag.toByte(flags) | opcode.getBitPattern()));
        encoded.add(1, (byte) 0);
        //encoded.add(1, (byte) 0b10000000);
        if (data.length <= 125) {
            encoded.set(1, (byte) (encoded.get(1) | data.length));
        } else if (data.length <= 65535) {
            encoded.set(1, (byte) (encoded.get(1) | 126));
            encoded.add(offset++, (byte) (data.length >> 8));
            encoded.add(offset++, (byte) (data.length));
        } else {
            encoded.set(1, (byte) (encoded.get(1) | 127));
            for (int i = 0; i < 8; i++) {
                encoded.add(offset++, (byte) ((data.length >> (64 - ((i + 1) * 8)))));
            }
        }
        // Messages sent from the server should not be masked
        /*
        byte[] mask = new byte[4];
        new Random().nextBytes(mask);
        for (int i = 0; i < mask.length; i++) {
            encoded.add(offset++, mask[i]);
        }
        for (int i = 0; i < data.length; i++) {
            encoded.add(offset++, (byte) (data[i] ^ mask[i % 4]));
        }*/
        for (int i = 0; i < data.length; i++) {
            encoded.add(offset++, data[i]);
        }
        return ArrayUtils.toPrimitiveArray(encoded.toArray(new Byte[encoded.size()]));
    }

    public List<byte[]> encodeAll() {
        List<byte[]> frames = new ArrayList<>();
        WSFrame current = this;
        while (true) {
            if (current.isLastFrame()) {
                current.setFlag(WSFlag.FIN);
                frames.add(current.encode());
                break;
            }
            frames.add(current.encode());
        }
        return frames;
    }
}
