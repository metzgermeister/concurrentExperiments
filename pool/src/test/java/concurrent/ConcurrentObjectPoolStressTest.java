package concurrent;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertTrue;

public class ConcurrentObjectPoolStressTest {
    private static final long LOCKUP_DETECT_TIMEOUT = 42 * 1000L;
    private ConcurrentObjectPool<Object> objectsPool;
    
    @Before
    public void setUp() throws Exception {
        objectsPool = new ConcurrentObjectPool<>();
        objectsPool.open();
    }
    
    @Test(timeout = LOCKUP_DETECT_TIMEOUT)
    public void shouldAcquireAndReleaseResources() throws Exception {
        final int totalResourcesCount = 10;
        final int totalConsumerThreads = 50;
        final int numberOfThreadOperations = 1000;
        
        List<Object> resources = new ArrayList<>(totalResourcesCount); //will be used for pool clearance
        
        for (int i = 0; i < totalResourcesCount; i++) {
            Object resource = new Object();
            objectsPool.add(resource);
            resources.add(resource);
        }
        
        final CyclicBarrier barrier = new CyclicBarrier(totalConsumerThreads + 1);
        
        for (int i = 0; i < totalConsumerThreads; i++) {
            new ConsumerThread(numberOfThreadOperations, barrier).start();
        }
        
        barrier.await(); // wait for all threads to complete work
    
        //all resources should be released by now so "remove" should not block
        resources.forEach(objectsPool::remove);
        
        //since we've got here there were no lost resources
        objectsPool.close();
        
    }
    
    private class ConsumerThread extends Thread {
        private final int workCycles;
        private final CyclicBarrier barrier;
        
        private ConsumerThread(int workCycles, CyclicBarrier barrier) {
            this.workCycles = workCycles;
            this.barrier = barrier;
        }
        
        @Override
        public void run() {
            assertTrue(objectsPool.isOpen());
            try {
                for (int i = 0; i < workCycles; i++) {
                    Object acquired = objectsPool.acquire();
                    sleep(1L); //force context switching
                    objectsPool.release(acquired);
                }
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
