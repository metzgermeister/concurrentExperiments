package matrix;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SerialMultiplierTest {
    
    private SerialMultiplier serialMultiplier = new SerialMultiplier();
    
    
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
        
        assertArrayEquals(C, serialMultiplier.multiply(A, B));
        assertArrayEquals(D, serialMultiplier.multiply(B, A));
        
    }
    
    
}