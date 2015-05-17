package concurrent;


import concurrent.exception.IllegalUsageException;
import concurrent.exception.ResourceNotAvailableException;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentObjectPool<R> implements ObjectPool<R> {

    private static final String ATTEMPT_TO_CLOSE_ALREADY_CLOSED_POOL = "attempt to close already closed pool";

    private final Lock resourcesLock = new ReentrantLock();

    private final Condition availableResourceIsPresent = resourcesLock.newCondition();

    private final Condition resourceReleased = resourcesLock.newCondition();

    private final AtomicBoolean isOpen = new AtomicBoolean(false);
    private final AtomicBoolean isClosing = new AtomicBoolean(false);

    private Set<R> acquiredResources = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private ConcurrentLinkedQueue<R> availableResources = new ConcurrentLinkedQueue<>();

    @Override
    public void open() {
        boolean opened = isOpen.compareAndSet(false, true);
        Validate.checkState(opened, "attempt to open already opened pool");
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void close() {
        Validate.checkState(isOpen.get(), ATTEMPT_TO_CLOSE_ALREADY_CLOSED_POOL);


        resourcesLock.lock();
        isClosing.set(true);

        try {
            closePoolIfNoAcquiredResources();
        } finally {
            isClosing.set(false);
            resourcesLock.unlock();
        }
    }

    @Override
    public void closeNow() {
        Validate.checkState(isOpen.get(), ATTEMPT_TO_CLOSE_ALREADY_CLOSED_POOL);
        closePool();
    }

    private void closePoolIfNoAcquiredResources() {
        while (!acquiredResources.isEmpty()) {
            try {
                resourceReleased.await();
            } catch (InterruptedException e) {
                onInterruptedException(e);
            }
        }

        closePool();
    }

    private void closePool() {
        boolean closed = isOpen.compareAndSet(true, false);
        Validate.checkState(closed, ATTEMPT_TO_CLOSE_ALREADY_CLOSED_POOL);
    }

    @Override
    public R acquire() {
        verifyIsOpen();
        Validate.checkState(!isClosing.get(), "attempt to acquire resource from closing pool");

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
        Validate.checkState(isOpen.get(), "pool must be open to handle calls for resources");
    }

    private void onInterruptedException(InterruptedException e) {
        System.out.println("exiting acquire by interrupted exception");
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
                System.out.println("exiting acquire by timeout");
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
        Validate.checkArgument(removed, "attempted to release unknown or not acquired resource");

        signalResourceUnlocking();

        add(resource);

    }

    private void signalResourceUnlocking() {
        resourcesLock.lock();
        try {
            resourceReleased.signalAll();
        } finally {
            resourcesLock.unlock();
        }
    }

    @Override
    public boolean add(R resource) {
        verifyIsOpen();

        resourcesLock.lock();
        boolean result;

        try {
            Validate.checkArgument(!acquiredResources.contains(resource), "attempt to add acquired resource");
            Validate.checkArgument(!availableResources.contains(resource), "attempt to add resource twice");
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

    @Override
    public boolean removeNow(R resource) {
        verifyIsOpen();

        boolean removed;
        resourcesLock.lock();
        try {
            removed = acquiredResources.remove(resource);
            if (!removed) {
                removed = availableResources.remove(resource);
            }
        } finally {
            resourcesLock.unlock();
        }
        return removed;
    }
    
}
