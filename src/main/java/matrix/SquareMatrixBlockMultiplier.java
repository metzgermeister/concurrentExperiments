package matrix;

import org.apache.commons.lang3.Validate;

import static matrix.ArraysUtil.copyBlockToMatrix;
import static matrix.ArraysUtil.finePrint;
import static matrix.ArraysUtil.getSubMatrix;

public class SquareMatrixBlockMultiplier extends SerialMultiplier {
    
    private final int blockSize;
    
    public SquareMatrixBlockMultiplier(int blockSize) {
        this.blockSize = blockSize;
    }
    
    @Override
    public Integer[][] multiply(Integer[][] a, Integer[][] b) {
        validate(a, b);
        validateSquare(a);
        validateSquare(b);
        validateDimension(a.length);
        validateDimension(b.length);
        int blocksInDimensionNum = a.length / blockSize;
        
        Integer[][] result = new Integer[a.length][a.length];
        
        for (int i = 0; i < blocksInDimensionNum; i++) {
            for (int j = 0; j < blocksInDimensionNum; j++) {
                Integer[][] horizontalStripe = getHorizontalStripe(a, j);
                Integer[][] verticalStripe = getVerticalStripe(b, i);
                Integer[][] resultBlock = super.multiply(horizontalStripe, verticalStripe);
                copyBlockToMatrix(result, i * blockSize, j * blockSize, resultBlock);
            }
        }
        return result;
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
