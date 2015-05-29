package matrix.sheduler;

import matrix.sheduler.comparators.MinMinComplexTaskComparator;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class MinMinTaskScheduler<ComplexTask> extends BasicTaskScheduler<ComplexTask> {
    private final Comparator comparator = new MinMinComplexTaskComparator();
    private final Queue<ComplexTask> tasks = new PriorityBlockingQueue<>(42, comparator);
    
    @Override
    protected Queue<ComplexTask> getQueue() {
        return tasks;
    }
    
    
}
