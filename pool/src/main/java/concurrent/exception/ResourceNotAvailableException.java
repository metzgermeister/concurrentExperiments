package concurrent.exception;

public class ResourceNotAvailableException extends RuntimeException {
    private static final long serialVersionUID = 5835376771448030020L;

    public ResourceNotAvailableException(String message) {
        super(message);
    }
}
