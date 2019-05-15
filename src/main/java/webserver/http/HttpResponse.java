package webserver.http;

import webserver.resource.FileResource;
import webserver.resource.Resource;
import webserver.util.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private Resource body;
    private Map<String, String> headers;
    private ZonedDateTime date;

    public static HttpResponse getResource(String name) {
        FileResource resource;
        try {
            resource = FileResource.fromFilename(name);
        } catch (HttpException ex) {
            return ex.makeResponse();
        }
        return HttpResponse.makeResponse(HttpStatus.OK, resource);
    }

    public static HttpResponse makeResponse(HttpStatus status, Resource resource) {
        HttpResponse response = new HttpResponse(status, resource);
        String contentType = resource.getMimeType().getName();
        if (resource.hasEncoding()) {
            contentType += "; charset=" + resource.getEncoding();
        }
        response.setHeader("Content-Type", contentType);
        return response;
    }
    public HttpResponse(HttpStatus status) {
        this.status = status;
        this.date = ZonedDateTime.now();
        this.headers = new HashMap<>();
    }
    public HttpResponse(HttpStatus status, Resource body) {
        this(status);
        this.body = body;
    }

    public boolean hasBody() {
        return body != null;
    }

    public Resource getBody() {
        return body;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public byte[] headerToBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toBytes() {
        byte[] header = headerToBytes();
        byte[] body = getBody().getBytes();
        byte[] result = new byte[header.length + body.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(body, 0, result, header.length - 1, body.length);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("HTTP/1.1 %d %s\r\n", status.getCode(), status.getMessage()));
        sb.append(String.format("Date: %s\r\n", date.format(DateTimeFormatter.RFC_1123_DATE_TIME)));
        for (String header : headers.keySet()) {
            sb.append(String.format("%s: %s\r\n", header, headers.get(header)));
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
