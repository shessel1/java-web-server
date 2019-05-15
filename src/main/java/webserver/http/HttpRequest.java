package webserver.http;

import webserver.util.CaseInsensitiveMap;

import java.util.*;

public class HttpRequest {
    private HttpMethod method;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String resourcePath;

    public static HttpRequest fromBytes(byte[] input) throws HttpException {
        return fromString(new String(input));
    }

    public static HttpRequest fromString(String input) throws HttpException {
        Map<String, String> parameters = new CaseInsensitiveMap();
        Map<String, String> headers = new CaseInsensitiveMap();
        String[] lines = input.split("\r?\n");
        if ((lines.length <= 0 || lines[0].isEmpty())) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Missing request parameters");
        }
        String[] requestParams = lines[0].split("\\s+");
        if (requestParams.length != 3) {
            System.out.println(input);
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid request parameters");
        }
        HttpMethod method = HttpMethod.fromString(requestParams[0]);
        String path = requestParams[1];
        if (path.contains("?")) {
            String[] params = path.split("\\?");
            path = params[0];
            for (String part : params[1].split("&")) {
                String[] param = part.split("=");
                parameters.put(param[0], param[1].trim());
            }
        }
        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(":", 2);
            if (!(header.length == 2)) {
                throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid header");
            }
            headers.put(header[0], header[1].trim());
        }
        return new HttpRequest(method, headers, parameters, path);
    }

    public HttpRequest(HttpMethod method,
                       Map<String, String> headers,
                       Map<String, String> parameters,
                       String resourcePath)
    {
        this.method = method;
        this.headers = headers;
        this.parameters = parameters;
        this.resourcePath = resourcePath;
    }

    public boolean isWSHandshake() {
        return headers.containsKey("Upgrade")
                && getHeaderValue("Upgrade").equals("websocket")
                && headers.containsKey("Sec-WebSocket-Key");
    }

    public List<String> getHeaderNames() {
        return new ArrayList<>(headers.keySet());
    }

    public String getHeaderValue(String name) {
        return headers.get(name);
    }

    public List<String> getParamaterNames() {
        return new ArrayList<>(parameters.keySet());
    }

    public String getParamValue(String name) {
        return parameters.get(name);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder params = new StringBuilder();
        if (parameters.size() != 0) {
            params.append("?");
            for (String param : parameters.keySet()) {
                params.append(String.format("%s=%s&", param, parameters.get(param)));
            }
            params.deleteCharAt(params.length() - 1);
        }
        sb.append(String.format("%s %s%s HTTP/1.1\r\n", method, resourcePath, params));
        for (String header : headers.keySet()) {
            sb.append(String.format("%s: %s\r\n", header, headers.get(header)));
        }
        return sb.toString();
    }
}