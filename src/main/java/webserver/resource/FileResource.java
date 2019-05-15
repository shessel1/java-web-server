package webserver.resource;

import webserver.Server;
import webserver.http.HttpException;
import webserver.http.HttpStatus;

import java.io.*;

public class FileResource extends Resource {
    private File file;

    private FileResource(File file, MimeType mimeType) {
        super(mimeType);
        this.file = file;
    }

    @Override
    public byte[] getBytes() {
        try {
            return getInputStream().readAllBytes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new byte[0];
    }

    public BufferedInputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public static FileResource fromFilename(String filename) throws HttpException {
        File file = new File(Server.getInstance().getResourcePath(), filename);
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            throw new HttpException(HttpStatus.NOT_FOUND,
                    String.format("Resource \"%s\"", filename));
        }
        return new FileResource(file, MimeType.fromString(filename));
    }
}
