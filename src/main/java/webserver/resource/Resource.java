package webserver.resource;

import java.nio.charset.Charset;

public abstract class Resource {
    protected MimeType mimeType;
    protected Charset encoding;

    public Resource(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public Resource(MimeType mimeType, Charset encoding) {
        this(mimeType);
        this.encoding = encoding;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public boolean hasEncoding() {
        return encoding != null;
    }

    public abstract byte[] getBytes();
}
