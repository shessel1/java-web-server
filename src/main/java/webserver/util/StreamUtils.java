package webserver.util;

import java.io.*;
public class StreamUtils {
    public static int readUntilBlank(InputStream stream, StringBuilder out) throws IOException {
        String line = "";
        int offset = 0;
        int c;
        while ((c = stream.read()) != -1) {
            offset++;
            line += (char) c;
            if (((char) c == '\r' && ((c = stream.read()) == '\n'))) {
                offset++;
                line += (char) c;
                if (line.isBlank()) {
                    return offset;
                } else {
                    out.append(line);
                    line = "";
                }
            }
        }
        return offset;
    }
}
