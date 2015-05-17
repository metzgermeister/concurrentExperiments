package matrix.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class MatrixUtilTest {
    
    @Test
    public void shouldMultiplyMatrices() throws Exception {
        Integer[][] A = {
                {0, 1},
                {0, 0}
        };
        
        Integer[][] B = {
                {0, 0},
                {1, 0}
        };
        
        Integer[][] C = {
                {1, 0},
                {0, 0}
        };
        
        
        Integer[][] D = {
                {0, 0},
                {0, 1}
        };
        
        
        Integer[][] AB = MatrixUtil.multiplySerial(A, B);
        assertArrayEquals(C, AB);
        
        Integer[][] BA = MatrixUtil.multiplySerial(B, A);
        assertArrayEquals(D, BA);
        
    }
    
    
}