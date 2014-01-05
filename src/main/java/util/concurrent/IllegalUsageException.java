package util.concurrent;

public class IllegalUsageException extends RuntimeException {

    private static final long serialVersionUID = 4562573109458049508L;

    public IllegalUsageException(String s) {
        super(s);
    }
}
