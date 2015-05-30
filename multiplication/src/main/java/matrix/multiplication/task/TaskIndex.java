package matrix.multiplication.task;

import java.io.Serializable;

public class TaskIndex implements Serializable {
    private final int horizontalBlockNum;
    private final int verticalBlockNum;
    private final int clientNumber;
    
    public TaskIndex(int horizontalBlockNum, int verticalBlockNum, int clientNumber) {
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
        this.clientNumber = clientNumber;
    }
    
    public TaskIndex(TaskIndex taskIndex) {
        this.horizontalBlockNum = taskIndex.horizontalBlockNum;
        this.verticalBlockNum = taskIndex.verticalBlockNum;
        this.clientNumber = taskIndex.clientNumber;
    }
    
    public int getHorizontalBlockNum() {
        return horizontalBlockNum;
    }
    
    public int getVerticalBlockNum() {
        return verticalBlockNum;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TaskIndex taskIndex = (TaskIndex) o;
        
        if (horizontalBlockNum != taskIndex.horizontalBlockNum) return false;
        if (verticalBlockNum != taskIndex.verticalBlockNum) return false;
        return clientNumber == taskIndex.clientNumber;
        
    }
    
    @Override
    public int hashCode() {
        int result = horizontalBlockNum;
        result = 31 * result + verticalBlockNum;
        result = 31 * result + clientNumber;
        return result;
    }
    
    @Override
    public String toString() {
        return "TaskIndex{" +
                "horizontalBlockNum=" + horizontalBlockNum +
                ", verticalBlockNum=" + verticalBlockNum +
                ", clientNumber=" + clientNumber +
                '}';
    }
}
