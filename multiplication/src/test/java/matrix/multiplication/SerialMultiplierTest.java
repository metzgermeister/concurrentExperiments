package matrix.multiplication;

import matrix.multiplication.task.MatrixMultiplyTask;
import org.junit.Assert;
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
        
        int irrelevant = 42;
        
        MatrixMultiplyTask task = new MatrixMultiplyTask(A, B, irrelevant, irrelevant, irrelevant);
        serialMultiplier.multiply(task);
        Assert.assertTrue(task.isCalculated());
        assertArrayEquals(C, task.getResult());
        
        MatrixMultiplyTask secondTask = new MatrixMultiplyTask(B, A, irrelevant, irrelevant, irrelevant);
        serialMultiplier.multiply(secondTask);
        Assert.assertTrue(secondTask.isCalculated());
        assertArrayEquals(D, secondTask.getResult());
        
    }
    
    
}