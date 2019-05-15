package webserver;

import webserver.http.*;
import webserver.net.Connection;
import webserver.net.RequestHandler;
import webserver.util.StreamUtils;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private Socket handle;

    public Client(Socket handle) {
        this.handle = handle;
    }

    @Override
    public void run() {
        Connection conn = null;
        try {
            conn = new Connection(handle);
            try {
                HttpRequest request = HttpRequest.fromBytes(conn.readMessage());
                RequestHandler handler = RequestHandler.getHandler(conn, request);
                handler.handle();
            } catch (HttpException ex) {
                conn.writeMessage(
                        new HttpException(HttpStatus.BAD_REQUEST,"Invalid HTTP request", ex)
                                .makeResponse().toBytes()
                );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        /*
        try (BufferedInputStream reader = new BufferedInputStream(handle.getInputStream());
             BufferedOutputStream writer = new BufferedOutputStream(handle.getOutputStream())) {
            HttpRequest request;
            try {
                request = readHttpHeader(reader);
            } catch (HttpException ex) {
                HttpResponse response = ex.makeResponse();
                writer.write(response.toBytes());
                writer.write(response.getBody().getBytes());
                return;
            }
            if (request.getMethod() == HttpMethod.GET && request.isWSHandshake()) {
                WSHandler handler = new WSHandler(request.getHeaderValue("Sec-WebSocket-Key"));
                writer.write(handler.accept().toBytes());
                writer.flush();
                // TODO: Move http/websocket specific logic to handlers
                // TODO: Handle websocket connection
                return;
            } else {
                HttpHandler handler = new HttpHandler(request);
                HttpResponse response = handler.getResponse();
                writer.write(response.toBytes());

                Resource resource = response.getBody();
                if (resource instanceof FileResource) {
                    ((FileResource)resource).getInputStream().transferTo(writer);
                } else {
                    writer.write(resource.getBytes());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                handle.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public HttpRequest readHttpHeader(InputStream reader) throws HttpException, IOException {
        StringBuilder header = new StringBuilder();
        StreamUtils.readUntilBlank(reader, header);
        return HttpRequest.fromString(header.toString());
    }
}
