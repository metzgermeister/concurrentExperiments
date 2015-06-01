package matrix.scheduler;

import matrix.scheduler.comparators.MaxComplexityFirstTaskComparator;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class MaxComplexityTaskScheduler<ComplexTask> extends BasicTaskScheduler<ComplexTask> {
    
    private final Comparator comparator = new MaxComplexityFirstTaskComparator();
    
    @SuppressWarnings("unchecked")
    private final Queue<ComplexTask> tasks = new PriorityBlockingQueue<>(42, comparator);
    
    @Override
    protected Queue<ComplexTask> getQueue() {
        return tasks;
    }
    
}
