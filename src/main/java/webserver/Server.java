package webserver;

import webserver.ws.WSEchoServer;
import webserver.ws.WSServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Server instance;
    private static WSServer wsServer = new WSEchoServer();

    private String resourcePath;
    private ServerSocket socket;
    private ExecutorService executors;

    public static Server getInstance() {
        return instance;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    private Server(String resourcePath, String host, int port) throws IOException {
        this.resourcePath = resourcePath;
        this.socket = new ServerSocket();
        this.socket.bind(new InetSocketAddress(host, port));
        this.executors = Executors.newCachedThreadPool();
    }

    private void listen() throws IOException {
        while (true) {
            executors.submit(new Client(socket.accept()));
        }
    }

    public static void main(String[] args) throws IOException {
        String resourcePath = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        instance = new Server(resourcePath, "localhost", 9090);
        instance.listen();
    }
}
