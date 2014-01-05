package util.concurrent;

import com.google.common.base.Preconditions;

import java.util.concurrent.TimeUnit;

public final class ConcurrentObjectPool<R> implements ObjectPool {
    private volatile boolean isOpen;

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public R acquire() {
        verifyIsOpen();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void verifyIsOpen() {
        Preconditions.checkState(isOpen, "pool must be open to acquire resource");
    }

    @Override
    public R acquire(long timeout, TimeUnit timeUnit) {
        verifyIsOpen();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
