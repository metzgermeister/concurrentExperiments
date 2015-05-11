package matrix.sheduler;

import java.util.List;

public interface TaskScheduler<T> {
    
    void submit(T task);
    
    T get();
    
    void submitAll(List<T> matrixMultiplyTasks);
    
    boolean hasTasks();
}
