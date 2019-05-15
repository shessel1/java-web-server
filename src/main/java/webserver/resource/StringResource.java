package webserver.resource;

import java.nio.charset.StandardCharsets;

public class StringResource extends Resource {
    private String message;

    public StringResource(String message) {
        super(MimeType.TEXT, StandardCharsets.UTF_8);
        this.message = message;
    }

    @Override
    public byte[] getBytes() {
        return message.getBytes(encoding);
    }
}
