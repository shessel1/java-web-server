package webserver.ws;

public enum WSStatus {
    NONE(0, "No Reason"),
    NORMAL_CLOSE(1000, "Normal Closure"),
    GOING_AWAY(1001, "Going Away"),
    PROTOCOL_ERROR(1002, "Protocol Error"),
    UNSUPPORTED_DATA(1003, "Unsupported Data"),
    INVALID_PAYLOAD(1007, "Invalid Payload Data");

    public byte[] toBytes() {
        return new byte[] { (byte) (code & 0xFF), (byte) ((code >> 8) & 0xFF)};
    }

    public static WSStatus fromFrame(WSFrame frame) throws WSException {
        if (frame.getOpcode() != WSOpcode.CLOSE) {
            throw new WSException("Only Close frames may define a status code");
        }
        byte[] data = frame.getData();
        if (data.length < 2) {
            return WSStatus.NONE;
        }
        short code = (short) (data[0] << 8 | data[1] & 0xFF);
        for (WSStatus status : WSStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new WSException(String.format("Unsupported status code \"%s\"", code));
    }

    private int code;
    private String message;

    WSStatus(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
