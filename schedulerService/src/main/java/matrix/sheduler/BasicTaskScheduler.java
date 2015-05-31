package matrix.sheduler;


import java.util.List;
import java.util.Queue;

public abstract class BasicTaskScheduler<T> implements TaskScheduler<T> {
    
    protected abstract Queue<T> getQueue();
    
    @Override
    public void submit(T task) {
        getQueue().add(task);
    }
    
    @Override
    public T get() {
        return getQueue().poll();
    }
    
    @Override
    public void submitAll(List<T> matrixMultiplyTasks) {
        getQueue().addAll(matrixMultiplyTasks);
    }
    
    @Override
    public boolean hasTasks() {
        return !getQueue().isEmpty();
    }
    
    @Override
    public int tasksCount() {
        return getQueue().size();
    }
    
    @Override
    public void clearTasks() {
        getQueue().clear();
        
    }
}
