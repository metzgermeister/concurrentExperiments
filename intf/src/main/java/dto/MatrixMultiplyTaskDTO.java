package dto;

import java.io.Serializable;

public class MatrixMultiplyTaskDTO implements Serializable {
    private Integer[][] a;
    private Integer[][] b;
    private int horizontalBlockNum;
    private int verticalBlockNum;
    
    public MatrixMultiplyTaskDTO() {
    }
    
    public MatrixMultiplyTaskDTO(Integer[][] a, Integer[][] b, int horizontalBlockNum, int verticalBlockNum) {
        this.a = a;
        this.b = b;
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
    }
    
    
    public Integer[][] getA() {
        return a;
    }
    
    public Integer[][] getB() {
        return b;
    }
    
    
    public int getHorizontalBlockNum() {
        return horizontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
    
    public void setA(Integer[][] a) {
        this.a = a;
    }
    
    public void setB(Integer[][] b) {
        this.b = b;
    }
    
    public void setHorizontalBlockNum(int horizontalBlockNum) {
        this.horizontalBlockNum = horizontalBlockNum;
    }
    
    public void setVerticalBlockNum(int verticalBlockNum) {
        this.verticalBlockNum = verticalBlockNum;
    }
}
