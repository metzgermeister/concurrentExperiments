package util.concurrent;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import util.concurrent.exception.IllegalUsageException;
import util.concurrent.exception.ResourceNotAvailableException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO check for addition of acquired resource
//TODO handle releasing removed resource (removeNow())
//TODO can't close closed pool
public final class ConcurrentObjectPool<R> implements ObjectPool<R> {

    Logger logger = Logger.getLogger(ConcurrentObjectPool.class);

    private final Lock availableResourcesLock = new ReentrantLock();

    private final Condition availableResourceIsPresent = availableResourcesLock.newCondition();

    //TODO make atomic
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
        availableResourcesLock.lock();

        try {
            while (availableResources.isEmpty()) {
                try {
                    availableResourceIsPresent.await();
                } catch (InterruptedException e) {
                    onInterruptedException();
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
        Preconditions.checkState(isOpen, "pool must be open to handle calls for resources");
    }

    private void onInterruptedException() {
        logger.error("exiting acquire by interrupted exception");
        throw new IllegalUsageException("resuming due to improper usage");
    }

    @Override
    public R acquire(long timeout, TimeUnit timeUnit) {
        R result;
        verifyIsOpen();

        boolean stillWaiting = true;
        availableResourcesLock.lock();

        try {
            while (availableResources.isEmpty()) {
                try {
                    if (!stillWaiting) {
                        logger.debug("exiting acquire by timeout");
                        throw new ResourceNotAvailableException("Resource is unavailable");
                    }
                    stillWaiting = availableResourceIsPresent.await(timeout, timeUnit);
                } catch (InterruptedException e) {
                    onInterruptedException();
                }
            }
            result = availableResources.poll();
            acquiredResources.add(result);

        } finally {
            availableResourcesLock.unlock();
        }

        return result;
    }


    @Override
    public void release(R resource) {
        verifyIsOpen();

        boolean removed = acquiredResources.remove(resource);
        Preconditions.checkArgument(removed, "attempted to release unknown or not acquired resource");

        add(resource);

    }

    @Override
    //TODO add mass test - 20 consumers and 1 producer
    public boolean add(R resource) {
        verifyIsOpen();

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
        verifyIsOpen();

        return availableResources.remove(resource);
    }

}
