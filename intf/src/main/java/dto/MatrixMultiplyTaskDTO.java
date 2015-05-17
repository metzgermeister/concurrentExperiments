package dto;

import java.io.Serializable;

public class MatrixMultiplyTaskDTO implements Serializable {
    private final Integer[][] a;
    private final Integer[][] b;
    final int horisontalBlockNum;
    final int verticalBlockNum;
    
    public MatrixMultiplyTaskDTO(Integer[][] a, Integer[][] b, int horisontalBlockNum, int verticalBlockNum) {
        this.a = a;
        this.b = b;
        this.horisontalBlockNum = horisontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
    }
    
    
    public Integer[][] getA() {
        return a;
    }
    
    public Integer[][] getB() {
        return b;
    }
    
    
    public int getHorisontalBlockNum() {
        return horisontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
}
