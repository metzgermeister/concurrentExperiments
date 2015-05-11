package matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ParallelBlockMatrixMultiplier extends SquareMatrixBlockMultiplier {
    
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    
    public ParallelBlockMatrixMultiplier(int blockSize) {
        super(blockSize);
    }
    
    @Override
    protected void processTasks(List<MatrixMultiplyTask> tasks) {
        List<Callable<Boolean>> callables = new ArrayList<>(tasks.size());
        
        for (MatrixMultiplyTask task : tasks) {
            Callable<Boolean> callable = () -> {
                long start = System.currentTimeMillis();
                multiply(task);
                long stop = System.currentTimeMillis();
                System.out.println("multiplied task " + task.getA().length + "x" + task.getA()[0].length
                        + " " + task.getB().length + "x" + task.getB()[0].length
                        + " in " + (stop - start) + " millis");
                return true;
            };
            callables.add(callable);
            
        }
        try {
            executor.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
