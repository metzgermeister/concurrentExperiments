package matrix.multiplication.task;

import java.io.Serializable;

public class TaskIndex implements Serializable {
    final int horizontalBlockNum;
    final int verticalBlockNum;
    
    public TaskIndex(int horizontalBlockNum, int verticalBlockNum) {
        this.horizontalBlockNum = horizontalBlockNum;
        this.verticalBlockNum = verticalBlockNum;
    }
    
    public TaskIndex(TaskIndex taskIndex) {
        this.horizontalBlockNum = taskIndex.horizontalBlockNum;
        this.verticalBlockNum = taskIndex.verticalBlockNum;
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
        return verticalBlockNum == taskIndex.verticalBlockNum;
        
    }
    
    @Override
    public int hashCode() {
        int result = horizontalBlockNum;
        result = 31 * result + verticalBlockNum;
        return result;
    }
    
    
    @Override
    public String toString() {
        return "TaskIndex{" +
                "horizontalBlockNum=" + horizontalBlockNum +
                ", verticalBlockNum=" + verticalBlockNum +
                '}';
    }
    
}
