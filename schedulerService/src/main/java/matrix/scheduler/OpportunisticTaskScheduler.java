package matrix.scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
public class OpportunisticTaskScheduler<T> extends BasicTaskScheduler<T> {
    
    private final Queue<T> tasks = new ConcurrentLinkedQueue<>();
    
    
    @Override
    protected Queue<T> getQueue() {
        return tasks;
    }
}
