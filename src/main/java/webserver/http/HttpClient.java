package webserver.http;

import webserver.net.Connection;
import webserver.net.RequestHandler;
import webserver.resource.FileResource;

import java.io.IOException;

public class HttpClient extends RequestHandler {
    private HttpRequest request;

    public HttpClient(Connection conn, HttpRequest request) {
        super(conn);
        this.request = request;
    }

    @Override
    public void handle() throws IOException {
        HttpResponse response;
        if (request.getMethod() == HttpMethod.GET) {
            response = HttpResponse.getResource(request.getResourcePath());
        } else {
            response = new HttpException(HttpStatus.BAD_REQUEST,
                    String.format("Method \"%s\" not implemented", request.getMethod()))
                    .makeResponse();
        }
        conn.writeMessage(response.headerToBytes());
        if ((response.getBody()) instanceof FileResource) {
            conn.writeStream(((FileResource)response.getBody()).getInputStream());
        } else {
            conn.writeMessage(response.getBody().getBytes());
        }
    }
}
