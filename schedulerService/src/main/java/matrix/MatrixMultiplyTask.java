package matrix;

public class MatrixMultiplyTask {
    private final Integer[][] a;
    private final Integer[][] b;
    private Integer[][] result;
    private volatile boolean calculated = false;
    final int horizontalBlockNum;
    final int verticalBlockNum;
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b, int horizontalBlockNum, int verticalBlockNum) {
        this.a = a;
        this.b = b;
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
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
    
    public int getHorizontalBlockNum() {
        return horizontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
}
