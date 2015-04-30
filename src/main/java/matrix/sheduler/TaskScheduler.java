package matrix.sheduler;

import matrix.MatrixMultiplyTask;

import java.util.List;

public interface TaskScheduler<T> {
    
    void submit(T task);
    
    T get();
    
    void submitAll(List<T> matrixMultiplyTasks);
}
