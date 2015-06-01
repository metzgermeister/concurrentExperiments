package matrix.multiplication;

import matrix.multiplication.task.MatrixMultiplyTask;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class SquareMatrixBlockMultiplier extends SerialMultiplier {
    
    private final int blockSize;
    
    
    public SquareMatrixBlockMultiplier(int blockSize) {
        this.blockSize = blockSize;
    }
    
    private void validateSquare(Integer[][] a, Integer[][] b) {
        validateSquare(a);
        validateSquare(b);
        validateDimension(a.length);
        validateDimension(b.length);
        
    }
    
    public Integer[][] multiply(Integer[][] a, Integer[][] b) {
        validateSquare(a, b);
        Integer[][] result = new Integer[a.length][a.length];
        List<MatrixMultiplyTask> tasks = TaskGenerator.generateMultiplyTasks(a, b, blockSize, 42);
        
        processTasks(tasks);
        
        gatherResults(result, tasks);
        return result;
    }
    
    protected void processTasks(List<MatrixMultiplyTask> tasks) {
        tasks.forEach(this::multiply);
    }
    
    private void gatherResults(Integer[][] result, List<MatrixMultiplyTask> tasks) {
        for (MatrixMultiplyTask task : tasks) {
            MatrixUtil.copyBlockToMatrix(result, task.getIndex().getHorizontalBlockNum() * blockSize,
                    task.getIndex().getVerticalBlockNum() * blockSize,
                    task.getResult());
        }
    }
    
    
    private void validateSquare(Integer[][] a) {
        Validate.isTrue(a.length == a[0].length);
    }
    
    private void validateDimension(int dimension) {
        Validate.isTrue(dimension % blockSize == 0, "blocksize " + blockSize + " is not divider of input " +
                "matrix dimension " + dimension);
    }
    
    
}
