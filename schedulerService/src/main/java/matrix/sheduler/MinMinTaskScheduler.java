package matrix.sheduler;

import java.util.List;

public class MinMinTaskScheduler<ComplexTask> implements TaskScheduler<ComplexTask> {
    
    @Override
    public void submit(ComplexTask task) {
        
    }
    
    @Override
    public ComplexTask get() {
        return null;
    }
    
    @Override
    public void submitAll(List<ComplexTask> matrixMultiplyTasks) {
        
    }
    
    @Override
    public boolean hasTasks() {
        return false;
    }
    
    @Override
    public int tasksCount() {
        return 0;
    }
    
    @Override
    public void clearTasks() {
        
    }
}
