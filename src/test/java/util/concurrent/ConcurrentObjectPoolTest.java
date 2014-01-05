package util.concurrent;

import org.junit.Before;
import org.junit.Test;

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
}
