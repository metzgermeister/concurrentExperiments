package util.concurrent;

public class ConcurrentObjectPool implements ObjectPool {
    private volatile boolean isOpen;

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

}
