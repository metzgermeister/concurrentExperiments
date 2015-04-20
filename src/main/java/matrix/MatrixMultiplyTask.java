package matrix;

public class MatrixMultiplyTask {
    private final Integer[][] a;
    private final Integer[][] b;
    private Integer[][] result;
    private boolean calculated = false;
    final int horisontalBlockNum;
    final int verticalBlockNum;
    
    public MatrixMultiplyTask(Integer[][] a, Integer[][] b, int horisontalBlockNum, int verticalBlockNum) {
        this.a = a;
        this.b = b;
        this.horisontalBlockNum = horisontalBlockNum;
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
    
    public int getHorisontalBlockNum() {
        return horisontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
}
