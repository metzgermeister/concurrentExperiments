package matrix.multiplication;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SquareMatrixBlockMultiplierTest {
    
    
    @Test
    public void shouldMultiplyMatrices() throws Exception {
        SquareMatrixBlockMultiplier multiplier = new SquareMatrixBlockMultiplier(1);
        Integer[][] A = {
                {1, 2, 3},
                {1, 1, 1},
                {1, 1, 1},
        };
        
        Integer[][] B = {
                {1, 0, 0},
                {0, 1, 0},
                {1, 0, 1},
        };
        
        Integer[][] C = {
                {4, 2, 3},
                {2, 1, 1},
                {2, 1, 1},
        };
        
        
        assertArrayEquals(C, multiplier.multiply(A, B));
        
    }
    
}