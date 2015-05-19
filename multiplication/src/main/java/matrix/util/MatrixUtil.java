package matrix.util;

import org.apache.commons.lang3.Validate;

import java.io.PrintStream;
import java.util.Random;

public final class MatrixUtil {
    private MatrixUtil() {
    }
    
    
    public static void randomize(Integer[][] matrix, Random random, int valuesUpperBound) {
        for (Integer[] col : matrix) {
            for (int i = 0; i < col.length; i++) {
                col[i] = random.nextInt(valuesUpperBound);
            }
        }
    }
    
    public static Integer[][] multiplySerial(Integer[][] a, Integer[][] b) {
        validate(a, b);
        
        int aRows = a.length;
        int aColumns = a[0].length;
        int bColumns = b[0].length;
        
        
        Integer[][] result = new Integer[aRows][bColumns];
        zero(result);
        
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                for (int k = 0; k < aColumns; k++) {
                    result[i][j] = result[i][j] + a[i][k] * b[k][j];
                }
                
            }
        }
        return result;
    }
    
    private static void validate(Integer[][] a, Integer[][] b) {
        Validate.noNullElements(a);
        Validate.noNullElements(b);
        
        int aColumns = a[0].length;
        int bRows = b.length;
        
        Validate.isTrue(aColumns == bRows, "A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
    }
    
    
    public static void copyBlockToMatrix(Integer[][] matrix, int startHorizontalPosition, int startVerticalPosition,
                                         Integer[][] block) {
        Validate.noNullElements(matrix);
        Validate.noNullElements(block);
        Validate.isTrue(startVerticalPosition < matrix.length, "start position is outside of matrix");
        Validate.isTrue(startHorizontalPosition < matrix[startVerticalPosition].length,
                "start position is outside of matrix");
        Validate.isTrue(startVerticalPosition + block.length <= matrix.length, "block wont fit matrix");
        Validate.isTrue(startHorizontalPosition + block[0].length <= matrix[startVerticalPosition].length, "block " +
                "wont fit matrix");
        
        for (int i = 0; i < block.length; i++) {
            Integer[] blockLine = block[i];
            Integer[] matrixLine = matrix[startVerticalPosition + i];
            Validate.isTrue(matrixLine.length >= blockLine.length + startHorizontalPosition, "block wont fit matrix");
            System.arraycopy(blockLine, 0, matrixLine, startHorizontalPosition, blockLine.length);
        }
        
    }
    
    public static void finePrint(Integer[][] a, PrintStream stream) {
        for (Integer[] anA : a) {
            for (Integer anB : anA) {
                stream.print(anB + " ");
            }
            stream.print("\n");
        }
        System.out.println("~~~~~~~~~~~~~");
    }
    
    
    public static void zero(Integer[][] matrix) {
        for (Integer[] a : matrix) {
            for (int i = 0; i < a.length; i++) {
                a[i] = 0;
            }
        }
    }
    
    public static Integer[][] getSubMatrix(Integer[][] matrix, int startHorizontalPosition, int startVerticalPosition,
                                           int horizontalSize, int verticalSize) {
        //TODO pivanenko think about using same matrix but with a different offset if memory allocation is too big 
        Validate.isTrue(verticalSize + startVerticalPosition <= matrix.length);
        Integer[][] subMatrix = new Integer[verticalSize][horizontalSize];
        for (int h = 0; h < verticalSize; h++) {
            Validate.isTrue(horizontalSize + startHorizontalPosition <= matrix[h + startVerticalPosition].length);
            Integer[] line = new Integer[horizontalSize];
            System.arraycopy(matrix[startVerticalPosition + h], startHorizontalPosition, line,
                    0, horizontalSize);
            subMatrix[h] = line;
        }
        return subMatrix;
    }
    
}
