package util.concurrent;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
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

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcquireFromClosedPool() throws Exception {
        pool.acquire();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAcquireWithTimeoutFromClosedPool() throws Exception {
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
            fail("something went wrong");
        }

        assertTrue("consumer haven't acquired resource", finallyAcquired.get());
    }

    @Test
    public void consumerShouldBeBlockedIfResourcesAreNotPresent() {
        pool.open();

        Thread consumer = new Thread() {
            public void run() {
                pool.acquire();
            }
        };

        try {
            consumer.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            consumer.interrupt();
            consumer.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse("consumer thread should be terminated", consumer.isAlive());
        } catch (Exception unexpected) {
            fail("something went wrong");
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
            fail("something went wrong");
        }

    }
}
