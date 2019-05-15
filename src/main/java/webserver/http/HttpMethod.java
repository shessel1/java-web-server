package webserver.http;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod fromString(String s) throws HttpException {
        switch (s.toUpperCase()) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            default:
                throw new HttpException(HttpStatus.BAD_REQUEST, String.format("Unknown method \"%s\"", s));
        }
    }
}
