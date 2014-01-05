package util.concurrent;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentObjectPool<R> implements ObjectPool<R> {

    Logger logger = Logger.getLogger(ConcurrentObjectPool.class);

    private final Lock availableResourcesLock = new ReentrantLock();

    //TODO availableResourceIsPresent should be signalled
    private final Condition availableResourceIsPresent = availableResourcesLock.newCondition();

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
    //TODO test somehow that resource moved to acquired list
    public R acquire() {
        R result;

        verifyIsOpen();
        availableResourcesLock.lock();

        try {
            while (availableResources.isEmpty()) {
                try {
                    availableResourceIsPresent.await();
                } catch (InterruptedException e) {
                    logger.error("was interrupted");
                    throw new IllegalUsageException("resuming due to improper usage");
                }
            }
            result = availableResources.poll();
            acquiredResources.add(result);

        } finally {
            availableResourcesLock.unlock();
        }

        return result;
    }

    private void verifyIsOpen() {
        Preconditions.checkState(isOpen, "pool must be open to acquire resource");
    }

    @Override
    public R acquire(long timeout, TimeUnit timeUnit) {
        verifyIsOpen();
        //TODO implement
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void release(R resource) {
        boolean removed = acquiredResources.remove(resource);
        Preconditions.checkArgument(removed, "attempted to release unknown or not acquired resource");

        add(resource);

    }

    @Override
    public boolean add(R resource) {
        availableResourcesLock.lock();
        boolean result;

        try {
            result = availableResources.add(resource);
            availableResourceIsPresent.signal();
        } finally {
            availableResourcesLock.unlock();
        }

        return result;
    }

    @Override
    public boolean remove(R resource) {
        return availableResources.remove(resource);
    }

}
