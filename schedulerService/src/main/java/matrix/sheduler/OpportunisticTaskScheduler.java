package matrix.sheduler;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
public class OpportunisticTaskScheduler<T> implements TaskScheduler<T> {
    
    private final Queue<T> tasks;
    
    public OpportunisticTaskScheduler() {
        tasks = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public void submit(T task) {
        tasks.add(task);
    }
    
    @Override
    public T get() {
        return tasks.poll();
    }
    
    @Override
    public void submitAll(List<T> matrixMultiplyTasks) {
        tasks.addAll(matrixMultiplyTasks);
    }
    
    @Override
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }
    
    @Override
    public int tasksCount() {
        return tasks.size();
    }
    
    @Override
    public void clearTasks() {
        tasks.clear();
        
    }
}
