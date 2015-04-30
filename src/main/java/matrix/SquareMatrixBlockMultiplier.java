package matrix;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

import static matrix.util.ArraysUtil.copyBlockToMatrix;
import static matrix.util.ArraysUtil.getSubMatrix;

public class SquareMatrixBlockMultiplier extends SerialMultiplier {
    
    //TODO pivanenko change to decomposition size
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
        List<MatrixMultiplyTask> tasks = generateMultiplyTasks(a, b, blockSize);
    
        processTasks(tasks);
    
        gatherResults(result, tasks);
        return result;
    }
    
    protected void processTasks(List<MatrixMultiplyTask> tasks) {
        tasks.forEach(this::multiply);
    }
    
    private void gatherResults(Integer[][] result, List<MatrixMultiplyTask> tasks) {
        for (MatrixMultiplyTask task : tasks) {
            copyBlockToMatrix(result, task.getHorisontalBlockNum() * blockSize, task.getVerticalBlockNum() * blockSize,
                    task.getResult());
        }
    }
    
    public List<MatrixMultiplyTask> generateMultiplyTasks(Integer[][] a, Integer[][] b, int blockSize) {
        int blocksInDimensionNum = a.length / blockSize;
        
        List<MatrixMultiplyTask> tasks = new ArrayList<>(blocksInDimensionNum * blocksInDimensionNum);
        
        
        for (int i = 0; i < blocksInDimensionNum; i++) {
            for (int j = 0; j < blocksInDimensionNum; j++) {
                Integer[][] horizontalStripe = getHorizontalStripe(a, j);
                Integer[][] verticalStripe = getVerticalStripe(b, i);
                MatrixMultiplyTask task = new MatrixMultiplyTask(horizontalStripe, verticalStripe, i, j);
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    private Integer[][] getHorizontalStripe(Integer[][] matrix, int verticalBlockIndex) {
        return getSubMatrix(matrix, 0, verticalBlockIndex * blockSize, matrix[0].length,
                blockSize);
    }
    
    private Integer[][] getVerticalStripe(Integer[][] matrix, int horizontalBlockIndex) {
        return getSubMatrix(matrix, horizontalBlockIndex * blockSize, 0, blockSize, matrix.length);
    }
    
    
    private void validateSquare(Integer[][] a) {
        Validate.isTrue(a.length == a[0].length);
    }
    
    private void validateDimension(int dimension) {
        Validate.isTrue(dimension % blockSize == 0, "blocksize " + blockSize + " is not divider of input " +
                "matrix dimension " + dimension);
    }
    
    
}
