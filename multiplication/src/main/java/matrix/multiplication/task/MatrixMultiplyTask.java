package matrix.multiplication.task;

public class MatrixMultiplyTask {
    private final Integer[][] a;
    private final Integer[][] b;
    private Integer[][] result;
    private volatile boolean calculated = false;
    private final TaskIndex taskIndex;
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b,
                              int horizontalBlockNum, int verticalBlockNum) {
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
}
