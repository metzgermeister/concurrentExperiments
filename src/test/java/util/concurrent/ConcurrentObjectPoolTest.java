package util.concurrent;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import util.concurrent.exception.IllegalUsageException;
import util.concurrent.exception.ResourceNotAvailableException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ConcurrentObjectPoolTest {

    private static final long LOCKUP_DETECT_TIMEOUT = 1000L;
    private ConcurrentObjectPool<String> pool;

    static {
        BasicConfigurator.configure();
    }

    @Before
    public void setUp() throws Exception {
        pool = new ConcurrentObjectPool<String>();
    }

    @Test
    public void shouldBeInitiallyClosed() throws Exception {
        assertFalse(pool.isOpen());
    }

    @Test
    public void shouldOpenPool() throws Exception {
        pool.open();
        assertTrue(pool.isOpen());
    }

    @Test
    public void shouldOpenFromOtherThread() throws Exception {
        Thread poolOpener = new Thread() {
            public void run() {
                pool.open();
            }
        };

        try {
            assertFalse(pool.isOpen());
            poolOpener.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            assertTrue(pool.isOpen());
        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcquireFromClosedPool() throws Exception {
        assertFalse(pool.isOpen());
        pool.acquire();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAddToClosedPool() throws Exception {
        assertFalse(pool.isOpen());
        pool.add("any");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddAlreadyAcquiredResource() throws Exception {
        pool.open();
        pool.add("resource");
        String acquired = pool.acquire();

        pool.add(acquired);

    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotOpenOpenedPool() {
        assertFalse(pool.isOpen());

        pool.open();
        assertTrue(pool.isOpen());

        pool.open();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotRemoveFromClosedPool() throws Exception {
        assertFalse(pool.isOpen());
        pool.remove("any");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotReleaseOnClosedPool() throws Exception {
        assertFalse(pool.isOpen());
        pool.release("any");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcquireWithTimeoutFromClosedPool() throws Exception {
        assertFalse(pool.isOpen());
        pool.acquire(42, TimeUnit.NANOSECONDS);
    }

    @Test
    public void shouldWakeUpBlockedConsumerByAddingResource() throws Exception {
        pool.open();

        final AtomicBoolean finallyAcquired = new AtomicBoolean(false);
        final String theOnlyResource = "TheOnlyResource";

        Thread consumer = new Thread() {
            public void run() {
                String acquired = pool.acquire();
                finallyAcquired.set(theOnlyResource.equals(acquired));
            }
        };

        try {
            consumer.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);

            assertTrue(pool.add(theOnlyResource));
            consumer.join(LOCKUP_DETECT_TIMEOUT);

            assertFalse("consumer should already acquire added resource", consumer.isAlive());
        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }

        assertTrue("consumer haven't acquired resource", finallyAcquired.get());
    }

    @Test
    public void shouldThrowExceptionOnInterrupt() {
        pool.open();

        final AtomicBoolean illegalUsageExceptionFaced = new AtomicBoolean(false);
        Thread consumer = new Thread() {
            public void run() {
                try {
                    pool.acquire();
                    fail("should never get here due to exception on interrupt()");
                } catch (IllegalUsageException e) {
                    illegalUsageExceptionFaced.set(true);
                }

            }
        };

        try {
            consumer.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            consumer.interrupt();
            consumer.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse("consumer thread should be terminated", consumer.isAlive());
            assertTrue("consumer thread should афсу IllegalUsageException", illegalUsageExceptionFaced.get());
        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }
    }

    @Test(timeout = LOCKUP_DETECT_TIMEOUT / 10)
    public void shouldAddAndAcquireResource() throws Exception {
        pool.open();

        String resource = "Resource";
        assertTrue(pool.add(resource));

        String acquired = pool.acquire();
        assertSame(acquired, resource);

    }

    @Test
    public void shouldRemoveResource() throws Exception {
        pool.open();
        String resource = "Some resource";
        pool.add(resource);

        assertTrue(pool.remove(resource));
    }

    @Test
    public void shouldNotRemoveUnknownResource() throws Exception {
        pool.open();

        pool.add("Some resource");
        assertFalse(pool.remove("unknown resource"));
    }

    @Test
    public void shouldAcquireAndReleaseResource() throws Exception {
        pool.open();

        String resource = "resource";
        pool.add(resource);

        String acquired = pool.acquire();
        assertEquals(resource, acquired);

        pool.release(acquired);

        String acquiredAgain = pool.acquire();
        assertEquals(resource, acquiredAgain);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotReleaseUnknownResource() throws Exception {
        pool.open();

        pool.release("unknown resource");
    }

    @Test(expected = ResourceNotAvailableException.class, timeout = LOCKUP_DETECT_TIMEOUT)
    public void shouldThrowExceptionOnAcquiringAfterTimeout() throws Exception {
        pool.open();
        pool.acquire(LOCKUP_DETECT_TIMEOUT / 10, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldAcquireWithTimeout() throws Exception {
        pool.open();
        String resource = "resource";
        pool.add(resource);

        int irrelevant = 7;
        assertEquals(resource, pool.acquire(irrelevant, TimeUnit.SECONDS));
    }

    @Test(timeout = LOCKUP_DETECT_TIMEOUT * 5)
    public void shouldAcquireAfterResourceWasReleased() throws Exception {
        pool.open();
        String singleResource = "SingleResource";
        pool.add(singleResource);
        String acquired = pool.acquire();

        Thread consumer = new Thread() {
            public void run() {
                Long bigTimeout = 42L;
                pool.acquire(bigTimeout, TimeUnit.SECONDS);
            }
        };

        try {
            consumer.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);

            pool.release(acquired);
            consumer.join(LOCKUP_DETECT_TIMEOUT);

            assertFalse("consumer thread should be terminated", consumer.isAlive());
        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }

    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCloseClosedPool() {
        assertFalse(pool.isOpen());
        pool.close();
    }

    @Test
    public void shouldClosePool() {
        pool.open();
        assertTrue(pool.isOpen());

        pool.close();
        assertFalse(pool.isOpen());
    }

    @Test
    public void shouldBlockWhenClosingPoolWithAcquiredResources() {
        pool.open();
        pool.add("first resource");
        pool.add("second resource");

        Thread oneWhoClosesPool = new Thread() {
            public void run() {
                pool.close();
            }
        };

        try {
            String acquiredOne = pool.acquire();
            String acquiredTwo = pool.acquire();

            oneWhoClosesPool.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            assertTrue("thread should be blocked since there are acquired resources", oneWhoClosesPool.isAlive());

            pool.release(acquiredOne);
            pool.release(acquiredTwo);
            oneWhoClosesPool.join(LOCKUP_DETECT_TIMEOUT);

            assertFalse("resources were released - thread should have been already finished his job",
                    oneWhoClosesPool.isAlive());

        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }

    }

    @Test
    public void shouldBlockOnRemovingAcquiredResource() {
        pool.open();
        final String resource = "resource";
        pool.add(resource);

        Thread oneWhoRemovesResource = new Thread() {
            public void run() {
                pool.remove(resource);
            }
        };

        try {
            String acquired = pool.acquire();
            assertEquals(resource, acquired);

            oneWhoRemovesResource.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            assertTrue("thread should be blocked since resource to be removed is acquired",
                    oneWhoRemovesResource.isAlive());

            pool.release(acquired);
            oneWhoRemovesResource.join(LOCKUP_DETECT_TIMEOUT);

            assertFalse("resources was released - remove operation should have completed",
                    oneWhoRemovesResource.isAlive());

        } catch (Exception unexpected) {
            fail("something went wrong: " + unexpected.getMessage());
        }

    }
}
