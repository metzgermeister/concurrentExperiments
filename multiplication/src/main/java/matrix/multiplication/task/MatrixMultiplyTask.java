package matrix.multiplication.task;

import org.apache.commons.lang3.Validate;
import scheduler.ComplexTask;

public class MatrixMultiplyTask implements ComplexTask {
    private final Integer[][] a;
    private final Integer[][] b;
    private Integer[][] result;
    private volatile boolean calculated = false;
    private final TaskIndex taskIndex;
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b,
                              int horizontalBlockNum, int verticalBlockNum) {
        Validate.notEmpty(a);
        Validate.notEmpty(b);
        this.a = a;
        this.b = b;
        
        this.taskIndex = new TaskIndex(horizontalBlockNum, verticalBlockNum);
    }
    
    public Integer[][] getResult() {
        return result;
    }
    
    public boolean isCalculated() {
        return calculated;
    }
    
    public void markCalculated() {
        calculated = true;
    }
    
    public Integer[][] getA() {
        return a;
    }
    
    public Integer[][] getB() {
        return b;
    }
    
    public void setResult(Integer[][] result) {
        this.result = result;
    }
    
    public TaskIndex getIndex() {
        return taskIndex;
    }
    
    @Override
    /**
     * A is  n x m      B is  m x n
     * complexity is O(n x n x m) 
     * each iteration is about one addition and one multiplication
     * @see matrix.util.MatrixUtil.multiplySerial
     */
    public int getComplexity() {
        
        return a.length * a[0].length * b[0].length;
    }
}
