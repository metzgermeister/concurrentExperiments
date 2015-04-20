package sheduler;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@SuppressWarnings("unused")
public class OpportunisticTaskScheduler<T> implements TaskScheduler<T> {
    
    private final Queue<T> tasks;
    
    public OpportunisticTaskScheduler(int capacity) {
        tasks = new ArrayBlockingQueue<>(capacity);
    }
    
    @Override
    public void submit(T task) {
        tasks.add(task);
        
    }
    
    @Override
    public T get() {
        return tasks.poll();
    }
}
