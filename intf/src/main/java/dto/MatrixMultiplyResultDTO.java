package dto;

import java.io.Serializable;

public class MatrixMultiplyResultDTO implements Serializable {
    private ExperimentStrategy strategy;
    
    private Integer[][] result;
    private int horizontalBlockNum;
    private int verticalBlockNum;
    private int clientNumber;
    
    public MatrixMultiplyResultDTO() {
    }
    
    
    public MatrixMultiplyResultDTO(Integer[][] result, int horizontalBlockNum, int verticalBlockNum,
                                   int clientNumber, ExperimentStrategy strategy) {
        this.result = result;
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
        this.clientNumber = clientNumber;
        this.strategy = strategy;
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
    
    public ExperimentStrategy getStrategy() {
        return strategy;
    }
    
    public void setStrategy(ExperimentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }
}
