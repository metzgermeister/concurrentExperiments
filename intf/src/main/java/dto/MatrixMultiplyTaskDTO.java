package dto;

import java.io.Serializable;

public class MatrixMultiplyTaskDTO implements Serializable {
    private Integer[][] a;
    private Integer[][] b;
    private int horizontalBlockNum;
    private int verticalBlockNum;
    private ExperimentStrategy experimentStrategy;
    private int clientNumber;
    
    public MatrixMultiplyTaskDTO() {
    }
    
    public MatrixMultiplyTaskDTO(Integer[][] a, Integer[][] b, int horizontalBlockNum, int verticalBlockNum,
                                 int clientNumber, ExperimentStrategy experimentStrategy) {
        this.a = a;
        this.b = b;
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
        this.clientNumber = clientNumber;
        this.experimentStrategy = experimentStrategy;
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
    
    public int getClientNumber() {
        return clientNumber;
    }
    
    public ExperimentStrategy getExperimentStrategy() {
        return experimentStrategy;
    }
    
    public void setExperimentStrategy(ExperimentStrategy experimentStrategy) {
        this.experimentStrategy = experimentStrategy;
    }
    
    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }
}
