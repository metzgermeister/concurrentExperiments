package matrix.util;

import org.junit.Assert;
import org.junit.Test;

public class ArraysUtilTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCopyBlockWithIncorrectOffset() throws Exception {
        Integer[][] A = {
                {0, 0},
                {0, 0}
        };
        
        Integer[][] block = {
                {1, 2},
                {3, 4}
        };
    
    
        MatrixUtil.copyBlockToMatrix(A, 1, 0, block);
        
    }
    
    @Test
    public void shouldCopyBlock() throws Exception {
        Integer[][] A = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        
        Integer[][] block = {
                {1, 2},
                {3, 4}
        };
        
        
        Integer[][] B = {
                {0, 0, 0},
                {0, 1, 2},
                {0, 3, 4}
        };
        MatrixUtil.copyBlockToMatrix(A, 1, 1, block);
        Assert.assertArrayEquals(B, A);
        
        
        Integer[][] C = {
                {1, 2, 0},
                {3, 4, 2},
                {0, 3, 4}
        };
    
        MatrixUtil.copyBlockToMatrix(A, 0, 0, block);
        Assert.assertArrayEquals(C, A);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetSubMatrixWithWrongOffset() throws Exception {
        Integer[][] A = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
    
        MatrixUtil.getSubMatrix(A, 1, 1, 4, 4);
    }
    
    @Test
    public void shouldGetSubMatrix() throws Exception {
        Integer[][] A = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        
        Integer[][] B = {
                {6, 7, 8},
                {10, 11, 12},
        };
        Assert.assertArrayEquals(B, MatrixUtil.getSubMatrix(A, 1, 1, 3, 2));
        
        Integer[][] C = {
                {1, 2},
                {5, 6},
                {9, 10},
                {13, 14}
        };
        
        Integer[][] subMatrix = MatrixUtil.getSubMatrix(A, 0, 0, 2, 4);
        Assert.assertArrayEquals(C, subMatrix);
    }
    
    
}