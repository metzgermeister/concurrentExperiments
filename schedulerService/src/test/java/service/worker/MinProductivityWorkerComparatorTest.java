package service.worker;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinProductivityWorkerComparatorTest {
    
    @Test
    public void shouldPutProductiveWorkerFirst() throws Exception {
        MinProductivityWorkerComparator comparator = new MinProductivityWorkerComparator();
        int irrelevantNumber = 42;
        String irrelevantString = "irrelevant";
        Worker strongWorker = new Worker(irrelevantString, irrelevantNumber, 100);
        Worker weakWorker = new Worker(irrelevantString, irrelevantNumber, 1);
        List<Worker> workers = new ArrayList<>();
        workers.add(weakWorker);
        workers.add(strongWorker);
        
        Collections.sort(workers, comparator);
        
        Assert.assertSame(weakWorker, workers.get(0));
        Assert.assertSame(strongWorker, workers.get(1));
    }
    
}
