package webserver.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WSEchoServer implements WSServer {
    private List<WSClient> clients;

    public WSEchoServer() {
        clients = new ArrayList<>();
    }

    @Override
    public void onClientConnected(WSClient client) {
        clients.add(client);
    }

    @Override
    public void onClientDisconnected(WSClient client) {
        clients.remove(client);
    }

    @Override
    public void onMessageRecieved(WSFrame message) throws WSException {
        if (message.getOpcode() != WSOpcode.TEXT) {
            throw new WSException("Server does not support non-text frames");
        }
        for (WSClient client : clients) {
            try {
                client.sendMessage(new String(message.getData()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
