package dto;

import java.io.Serializable;

public class MatrixMultiplyResultDTO implements Serializable {
    
    private Integer[][] result;
    private int horizontalBlockNum;
    private int verticalBlockNum;
    private int clientNumber;
    
    public MatrixMultiplyResultDTO() {
    }
    
    
    public MatrixMultiplyResultDTO(Integer[][] result, int horizontalBlockNum, int verticalBlockNum, int clientNumber) {
        this.result = result;
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
        this.clientNumber = clientNumber;
    }
    
    public Integer[][] getResult() {
        return result;
    }
    
    public int getHorizontalBlockNum() {
        return horizontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
    
    public void setResult(Integer[][] result) {
        this.result = result;
    }
    
    public void setHorizontalBlockNum(int horizontalBlockNum) {
        this.horizontalBlockNum = horizontalBlockNum;
    }
    
    public void setVerticalBlockNum(int verticalBlockNum) {
        this.verticalBlockNum = verticalBlockNum;
    }
    
    public int getClientNumber() {
        return clientNumber;
    }
    
}
