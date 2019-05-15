package webserver.net;

import webserver.http.HttpRequest;
import webserver.util.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private Socket handle;
    private BufferedInputStream input;
    private BufferedOutputStream output;

    public Connection(Socket handle) throws IOException {
        this.handle = handle;
        this.input = new BufferedInputStream(handle.getInputStream());
        this.output = new BufferedOutputStream(handle.getOutputStream());
    }

    public Socket getHandle() {
        return handle;
    }

    public byte[] readMessage() throws IOException {
        List<Byte> buffer = new ArrayList<>(512);
        byte b;
        while ((b = (byte) input.read()) != -1) {
            buffer.add(b);
            if (input.available() <= 0) {
                break;
            }
        }
        return ArrayUtils.toPrimitiveArray(buffer.toArray(new Byte[buffer.size()]));
    }

    public void writeStream(InputStream stream) throws IOException {
        stream.transferTo(output);
        output.flush();
    }

    public void writeMessage(byte[] message) throws IOException {
        output.write(message);
        output.flush();
    }

    public void disconnect() throws IOException {
        if (handle.isClosed()) {
            throw new IllegalStateException("Connection is already closed");
        }
        output.flush();
        handle.close();
    }
}
