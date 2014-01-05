package util.concurrent;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConcurrentObjectPoolTest {

    private static final long LOCKUP_DETECT_TIMEOUT = 1000L;
    private ConcurrentObjectPool<String> pool;

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

    @Test
    public void shouldAcquireResource() throws Exception {
//        pool.

    }
}
