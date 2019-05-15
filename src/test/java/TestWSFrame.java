import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.ws.WSException;
import webserver.ws.WSFlag;
import webserver.ws.WSFrame;
import webserver.ws.WSOpcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestWSFrame {
    @Test
    @DisplayName("A WebSocket frame is encoded & decoded")
    public void testEncodeDecodeWSFrame() throws WSException {
        String data = "Hello World!";
        Set<WSFlag> flags = new HashSet<>(Arrays.asList(WSFlag.FIN, WSFlag.RSV1));
        WSFrame frame = new WSFrame(WSOpcode.TEXT, flags, data.getBytes());
        WSFrame decoded = WSFrame.decode(frame.encode());

        Assertions.assertEquals(WSOpcode.TEXT, decoded.getOpcode());
        Assertions.assertIterableEquals(flags, decoded.getFlags());
        Assertions.assertEquals(data, new String(decoded.getData()));
    }
}
