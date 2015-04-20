package sheduler;

public interface TaskScheduler<T> {
    
    void submit(T task);
    
    T get();
    
}
