package util.concurrent.exception;

public class IllegalUsageException extends RuntimeException {

    private static final long serialVersionUID = 4562573109458049508L;

    public IllegalUsageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
