package webserver.ws;

import webserver.Server;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.net.Connection;
import webserver.net.RequestHandler;
import webserver.util.CryptoUtils;

import java.io.IOException;
import java.util.Base64;

public class WSClient extends RequestHandler {
    public static final String MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private WSServer server;
    private String key;

    public WSClient(WSServer server, Connection conn, String key) {
        super(conn);
        this.server = server;
        this.key = key;
    }

    public HttpResponse accept() {
        HttpResponse response = new HttpResponse(HttpStatus.SWITCHING_PROTOCOLS);
        String accept = Base64.getEncoder().encodeToString(
                CryptoUtils.hash(CryptoUtils.SHA1, key, MAGIC_STRING)
        );
        response.setHeader("Sec-WebSocket-Accept", accept);
        response.setHeader("Connection", "upgrade");
        response.setHeader("Upgrade", "WebSocket");
        return response;
    }

    public void sendMessage(String message) throws IOException {
        sendFrame(new WSFrame(WSOpcode.TEXT, WSFlag.FIN, message.getBytes()));
    }

    public void sendFrame(WSFrame frame) throws IOException {
        conn.writeMessage(frame.encode());
    }

    @Override
    public void handle() throws IOException {
        conn.writeMessage(accept().headerToBytes());
        server.onClientConnected(this);
        while (true) {
            if (conn.getHandle().isClosed()) {
                System.out.println("Client unexpectedly closed the connection");
                break;
            }
            try {
                WSFrame frame = WSFrame.decode(conn.readMessage());
                if (frame.getOpcode() == WSOpcode.CLOSE) {
                    try {
                        WSStatus status = WSStatus.fromFrame(frame);
                        System.out.println(String.format("Client closing connection %d %s", status.getCode(), status.getMessage()));
                    } catch (WSException ex) {
                        System.out.println("Client closing connection for unknown reason");
                    }
                    break;
                }
                server.onMessageRecieved(frame);
            } catch (WSException ex) {
                //WSFrame error = new WSFrame(WSOpcode.TEXT, WSFlag.FIN, WSStatus.UNSUPPORTED_DATA.toBytes());
                //conn.writeMessage(error.encode());
                ex.printStackTrace();
            }
        }
        server.onClientDisconnected(this);
    }
}
