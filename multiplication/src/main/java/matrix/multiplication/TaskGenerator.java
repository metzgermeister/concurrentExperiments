package matrix.multiplication;

import dto.ExperimentStrategy;
import matrix.multiplication.task.MatrixMultiplyTask;
import matrix.util.MatrixUtil;

import java.util.ArrayList;
import java.util.List;

public final class TaskGenerator {
    
    private TaskGenerator() {
    }
    
    /**
     * for cases when strategy is irrelevant
     */
    public static List<MatrixMultiplyTask> generateMultiplyTasks(Integer[][] a, Integer[][] b, int blockSize, int
            clientNumber) {
        return generateMultiplyTasks(a, b, blockSize, clientNumber, ExperimentStrategy.MINMIN);
    }
    
    public static List<MatrixMultiplyTask> generateMultiplyTasks(Integer[][] a, Integer[][] b, int blockSize, int
            clientNumber, ExperimentStrategy strategy) {
        int blocksInDimensionNum = a.length / blockSize;
        
        List<MatrixMultiplyTask> tasks = new ArrayList<>(blocksInDimensionNum * blocksInDimensionNum);
        
        
        for (int i = 0; i < blocksInDimensionNum; i++) {
            for (int j = 0; j < blocksInDimensionNum; j++) {
                Integer[][] horizontalStripe = getHorizontalStripe(a, j, blockSize);
                Integer[][] verticalStripe = getVerticalStripe(b, i, blockSize);
                MatrixMultiplyTask task = new MatrixMultiplyTask(horizontalStripe, verticalStripe, i, j,
                        clientNumber, strategy);
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    private static Integer[][] getHorizontalStripe(Integer[][] matrix, int verticalBlockIndex, int blockSize) {
        return MatrixUtil.getSubMatrix(matrix, 0, verticalBlockIndex * blockSize, matrix[0].length,
                blockSize);
    }
    
    private static Integer[][] getVerticalStripe(Integer[][] matrix, int horizontalBlockIndex, int blockSize) {
        return MatrixUtil.getSubMatrix(matrix, horizontalBlockIndex * blockSize, 0, blockSize, matrix.length);
    }
}
