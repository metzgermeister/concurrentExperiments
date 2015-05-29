package service.worker;

import concurrent.ConcurrentObjectPool;
import concurrent.ObjectPool;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaxProductivityWorkerComparatorTest {
    @Test
    public void shouldPutProductiveWorkerFirst() throws Exception {
        MaxProductivityWorkerComparator comparator = new MaxProductivityWorkerComparator();
        int irrelevantNumber = 42;
        String irrelevantString = "irrelevant";
        Worker strongWorker = new Worker(irrelevantString, irrelevantNumber, 100);
        Worker weakWorker = new Worker(irrelevantString, irrelevantNumber, 1);
        List<Worker> workers = new ArrayList<>();
        workers.add(weakWorker);
        workers.add(strongWorker);
        
        Collections.sort(workers, comparator);
        
        Assert.assertSame(strongWorker, workers.get(0));
        Assert.assertSame(weakWorker, workers.get(1));
        
    }
    
    @Test
    public void shouldWorkForPool() throws Exception {
        MaxProductivityWorkerComparator comparator = new MaxProductivityWorkerComparator();
        
        int irrelevantNumber = 42;
        String irrelevantString = "irrelevant";
        Worker strongWorkerOne = new Worker(irrelevantString, irrelevantNumber, 100);
        Worker strongWorkerTwo = new Worker(irrelevantString, irrelevantNumber, 100);
        Worker weakWorkerOne = new Worker(irrelevantString, irrelevantNumber, 1);
        Worker weakWorkerTwo = new Worker(irrelevantString, irrelevantNumber, 1);
        
        
        ObjectPool<Worker> workersPool = new ConcurrentObjectPool<>(comparator);
        workersPool.open();
        
        workersPool.add(weakWorkerOne);
        workersPool.add(weakWorkerTwo);
        workersPool.add(strongWorkerOne);
        workersPool.add(strongWorkerTwo);
        
//        Collections.sort(workersPool, comparator);
        
        //priority should be stable sorted
        Assert.assertSame(strongWorkerOne, workersPool.acquire());
        Assert.assertSame(strongWorkerTwo, workersPool.acquire());
        Assert.assertSame(weakWorkerOne, workersPool.acquire());
        Assert.assertSame(weakWorkerTwo, workersPool.acquire());
        
        workersPool.release(weakWorkerOne);
        workersPool.release(strongWorkerOne);
        
        Assert.assertSame(strongWorkerOne, workersPool.acquire());
        Assert.assertSame(weakWorkerOne, workersPool.acquire());
        
        
    }
}