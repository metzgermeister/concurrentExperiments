package util.concurrent;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentObjectPool<R> implements ObjectPool {

    Logger logger = Logger.getLogger(ConcurrentObjectPool.class);

    private final Lock lock = new ReentrantLock();

    //TODO availableResourceIsPresent should be signalled
    private final Condition availableResourceIsPresent = lock.newCondition();

    private volatile boolean isOpen;

    //TODO think about more efficient way - releasing requires a lookup through all collection
    private ConcurrentLinkedQueue<R> acquiredResources = new ConcurrentLinkedQueue<R>();

    private ConcurrentLinkedQueue<R> availableResources = new ConcurrentLinkedQueue<R>();

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
        R result;

        verifyIsOpen();
        lock.lock();

        try {
            while (availableResources.isEmpty()) {
                try {
                    availableResourceIsPresent.await();
                } catch (InterruptedException e) {
                    logger.error("was interrupted");
                    throw new IllegalUsageError("resuming due to improper usage");
                }
            }
            result = availableResources.poll();

        } finally {
            lock.unlock();
        }

        return result;  //To change body of implemented methods use File | Settings | File Templates.
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
