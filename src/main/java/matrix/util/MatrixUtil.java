package matrix.util;

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
}
