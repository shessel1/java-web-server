package webserver.net;

import webserver.http.*;
import webserver.ws.WSClient;
import webserver.ws.WSEchoServer;
import webserver.ws.WSServer;

import java.io.IOException;

public abstract class RequestHandler {
    private static final WSServer WSServer = new WSEchoServer();

    protected Connection conn;

    public RequestHandler(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    public abstract void handle() throws IOException;

    public static RequestHandler getHandler(Connection conn, HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET && request.isWSHandshake()) {
            return new WSClient(WSServer, conn, request.getHeaderValue("Sec-WebSocket-Key"));
        } else {
            return new HttpClient(conn, request);
        }
    }
}
