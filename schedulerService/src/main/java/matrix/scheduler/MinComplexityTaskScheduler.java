package matrix.scheduler;

import matrix.scheduler.comparators.MinComplexityFirstTaskComparator;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class MinComplexityTaskScheduler<ComplexTask> extends BasicTaskScheduler<ComplexTask> {
    private final Comparator comparator = new MinComplexityFirstTaskComparator();
    @SuppressWarnings("unchecked")
    private final Queue<ComplexTask> tasks = new PriorityBlockingQueue<>(42, comparator);
    
    @Override
    protected Queue<ComplexTask> getQueue() {
        return tasks;
    }
    
    
}
