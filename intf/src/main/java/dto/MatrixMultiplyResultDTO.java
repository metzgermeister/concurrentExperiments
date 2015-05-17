package dto;

import java.io.Serializable;

public class MatrixMultiplyResultDTO implements Serializable {
    
    private Integer[][] result;
    private int horisontalBlockNum;
    private int verticalBlockNum;
    
    public MatrixMultiplyResultDTO() {
    }
    
    
    public MatrixMultiplyResultDTO(Integer[][] result, int horisontalBlockNum, int verticalBlockNum) {
        this.result = result;
        this.horisontalBlockNum = horisontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
    }
    
    public Integer[][] getResult() {
        return result;
    }
    
    public int getHorisontalBlockNum() {
        return horisontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
    
    public void setResult(Integer[][] result) {
        this.result = result;
    }
    
    public void setHorisontalBlockNum(int horisontalBlockNum) {
        this.horisontalBlockNum = horisontalBlockNum;
    }
    
    public void setVerticalBlockNum(int verticalBlockNum) {
        this.verticalBlockNum = verticalBlockNum;
    }
}
