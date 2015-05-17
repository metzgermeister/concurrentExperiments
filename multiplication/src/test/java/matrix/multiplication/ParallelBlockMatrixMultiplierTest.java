package matrix.multiplication;

import matrix.util.MatrixUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class ParallelBlockMatrixMultiplierTest {
    
    @Test
    public void shouldMultiplyMatrices() throws Exception {
        ParallelBlockMatrixMultiplier multiplier = new ParallelBlockMatrixMultiplier(1);
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
    
    @Test(timeout = 60000)
    public void shouldHandleBigOne() throws Exception {
        ParallelBlockMatrixMultiplier multiplier = new ParallelBlockMatrixMultiplier(1000);
        int size = 1000;
        Integer[][] A = new Integer[size][size];
        Integer[][] B = new Integer[size][size];
        Random random = new Random();
        MatrixUtil.randomize(A, random, 100);
        System.out.println("randomized a");
        MatrixUtil.randomize(B, random, 100);
        System.out.println("randomized b");
        
        
        long start = System.currentTimeMillis();
        Integer[][] parallelResult = multiplier.multiply(A, B);
        long stop = System.currentTimeMillis();
        System.out.println("took " + (stop - start));
        
        SerialMultiplier serialMultiplier = new SerialMultiplier();
        Integer[][] serialResult = serialMultiplier.multiplySerial(A, B);
        
        Assert.assertArrayEquals(serialResult, parallelResult);
    }
    
    
}