package util.concurrent;

public class IllegalUsageError extends RuntimeException {

    private static final long serialVersionUID = 4562573109458049508L;

    public IllegalUsageError(String s) {
        super(s);
    }
}
