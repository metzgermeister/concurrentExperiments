package matrix.multiplication.task;

import org.apache.commons.lang3.Validate;
import scheduler.ComplexTask;

public class MatrixMultiplyTask implements ComplexTask {
    private final Integer[][] a;
    private final Integer[][] b;
    private Integer[][] result;
    private volatile boolean calculated = false;
    private final TaskIndex taskIndex;
    private final Integer priority;
    
    private final static Integer defaultPriority = 1;
    private int clientNumber;
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b,
                              int horizontalBlockNum, int verticalBlockNum,
                              int priority, int clientNumber) {
        Validate.notEmpty(a);
        Validate.notEmpty(b);
        this.a = a;
        this.b = b;
        
        this.taskIndex = new TaskIndex(horizontalBlockNum, verticalBlockNum, clientNumber);
        this.clientNumber = clientNumber;
        this.priority = priority;
    }
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b,
                              int horizontalBlockNum, int verticalBlockNum, int clientNumber) {
        this(a, b, horizontalBlockNum, verticalBlockNum, defaultPriority, clientNumber);
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
    public Integer getComplexity() {
        return a.length * a[0].length * b[0].length;
    }
    
    @Override
    public Integer getPriority() {
        return priority;
    }
    
    public int getClientNumber() {
        return clientNumber;
    }
    
    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }
}
