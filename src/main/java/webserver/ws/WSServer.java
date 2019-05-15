package webserver.ws;

public interface WSServer {
    void onClientConnected(WSClient client);
    void onClientDisconnected(WSClient client);
    void onMessageRecieved(WSFrame message) throws WSException;
}
