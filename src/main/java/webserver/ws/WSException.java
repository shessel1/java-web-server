package webserver.ws;

public class WSException extends Exception {
    public WSException(Throwable cause) {
        super(cause);
    }

    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSException(String message) {
        super(message);
    }
}
