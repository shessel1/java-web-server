package webserver.http;

import webserver.resource.StringResource;

public class HttpException extends Exception {
    private HttpStatus status;

    public HttpException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpException(HttpStatus status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpResponse makeResponse() {
        StringBuilder response = new StringBuilder();
        response.append(String.format("Encountered error %d %s", status.getCode(), status.getMessage()));
        if (getMessage() != null) {
            response.append("\r\n");
            response.append(getMessage());
            response.append("\r\n");
        }
        if (getCause() != null) {
            for (StackTraceElement element : getCause().getStackTrace()) {
                response.append(String.format("%s\r\n", element));
            }
        }
        return HttpResponse.makeResponse(status, new StringResource(response.toString()));
    }

    public HttpStatus getStatus() {
        return status;
    }
}
