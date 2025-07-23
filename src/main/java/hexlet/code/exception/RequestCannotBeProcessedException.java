package hexlet.code.exception;

public class RequestCannotBeProcessedException extends RuntimeException {
    public RequestCannotBeProcessedException(String message) {
        super(message);
    }
}
