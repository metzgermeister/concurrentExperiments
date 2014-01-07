package util.concurrent;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import util.concurrent.exception.IllegalUsageException;
import util.concurrent.exception.ResourceNotAvailableException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO handle releasing removed resource (after removeNow() method)
public final class ConcurrentObjectPool<R> implements ObjectPool<R> {

    Logger logger = Logger.getLogger(ConcurrentObjectPool.class);

    private final Lock resourcesLock = new ReentrantLock();

    private final Condition availableResourceIsPresent = resourcesLock.newCondition();

    private final Condition resourceReleased = resourcesLock.newCondition();

    private final AtomicBoolean isOpen = new AtomicBoolean(false);
    private final AtomicBoolean isClosing = new AtomicBoolean(false);

    // TODO think about more efficient way - releasing requires a lookup through all collection -
    // ConcurrentHashMap?
    private ConcurrentLinkedQueue<R> acquiredResources = new ConcurrentLinkedQueue<R>();

    private ConcurrentLinkedQueue<R> availableResources = new ConcurrentLinkedQueue<R>();

    @Override
    public void open() {
        boolean opened = isOpen.compareAndSet(false, true);
        Preconditions.checkState(opened, "attempt to open already opened pool");
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void close() {
        Preconditions.checkState(isOpen.get(), "attempt to close already closed pool");


        resourcesLock.lock();
        isClosing.set(true);

        try {
            closePoolIfNoAcquiredResources();
        } finally {
            isClosing.set(false);
            resourcesLock.unlock();
        }
    }

    private void closePoolIfNoAcquiredResources() {
        while (!acquiredResources.isEmpty()) {
            try {
                resourceReleased.await();
            } catch (InterruptedException e) {
                onInterruptedException(e);
            }
        }

        boolean closed = isOpen.compareAndSet(true, false);
        Preconditions.checkState(closed, "attempt to close already closed pool");
    }

    @Override
    public R acquire() {
        verifyIsOpen();
        Preconditions.checkState(!isClosing.get(),"attempt to acquire resource from closing pool");

        resourcesLock.lock();

        R result;
        try {
            result = acquireIfAvailable();
        } finally {
            resourcesLock.unlock();
        }

        return result;
    }

    private R acquireIfAvailable() {
        R result;
        while (availableResources.isEmpty()) {
            try {
                availableResourceIsPresent.await();
            } catch (InterruptedException e) {
                onInterruptedException(e);
            }
        }
        result = availableResources.poll();
        acquiredResources.add(result);
        return result;
    }

    private void verifyIsOpen() {
        Preconditions.checkState(isOpen.get(), "pool must be open to handle calls for resources");
    }

    private void onInterruptedException(InterruptedException e) {
        logger.error("exiting acquire by interrupted exception");
        throw new IllegalUsageException("resuming due to improper usage", e);
    }

    @Override
    public R acquire(long timeout, TimeUnit timeUnit) {
        R result;
        verifyIsOpen();

        boolean stillWaiting = true;
        resourcesLock.lock();

        try {
            while (availableResources.isEmpty()) {
                stillWaiting = waitForResourceWithTimeout(timeout, timeUnit, stillWaiting);
            }
            result = availableResources.poll();
            acquiredResources.add(result);

        } finally {
            resourcesLock.unlock();
        }

        return result;
    }

    private boolean waitForResourceWithTimeout(long timeout, TimeUnit timeUnit, boolean stillWaiting) {
        try {
            if (!stillWaiting) {
                logger.debug("exiting acquire by timeout");
                throw new ResourceNotAvailableException("Resource is unavailable");
            }
            stillWaiting = availableResourceIsPresent.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            onInterruptedException(e);
        }
        return stillWaiting;
    }

    @Override
    public void release(R resource) {
        verifyIsOpen();

        boolean removed = acquiredResources.remove(resource);
        Preconditions.checkArgument(removed, "attempted to release unknown or not acquired resource");

        signalResourceUnlocking();

        add(resource);

    }

    private void signalResourceUnlocking() {
        resourcesLock.lock();
        try {
            resourceReleased.signal();
        } finally {
            resourcesLock.unlock();
        }
    }

    @Override
    // TODO add mass test - 20 consumers and 1 producer
    public boolean add(R resource) {
        verifyIsOpen();

        Preconditions.checkArgument(!acquiredResources.contains(resource), "attempt to add acquired resource");

        resourcesLock.lock();
        boolean result;

        try {
            result = availableResources.add(resource);
            availableResourceIsPresent.signal();
        } finally {
            resourcesLock.unlock();
        }

        return result;
    }

    @Override
    public boolean remove(R resource) {
        verifyIsOpen();

        boolean removed = false;
        resourcesLock.lock();
        try {
            while (acquiredResources.contains(resource)) {
                resourceReleased.await();
            }
            removed = availableResources.remove(resource);

        } catch (InterruptedException e) {
            onInterruptedException(e);
        } finally {
            resourcesLock.unlock();
        }

        return removed;
    }

}
