package util.concurrent;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ConcurrentObjectPoolTest {

    private ConcurrentObjectPool pool;

    @Before
    public void setUp() throws Exception {
        pool = new ConcurrentObjectPool();
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
}
