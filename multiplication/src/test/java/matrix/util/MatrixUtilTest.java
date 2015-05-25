package matrix.util;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

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
    
    @Test
    public void shouldWriteTask() throws Exception {
        int aDim = 1200;
        int bDim = 600;
        Integer[][] a = new Integer[aDim][bDim];
        Integer[][] b = new Integer[bDim][aDim];
        
        Random random = new Random();
        MatrixUtil.randomize(a, random, 100);
        MatrixUtil.randomize(b, random, 100);
        PrintStream stream = new PrintStream(new FileOutputStream("/tmp/matrixTask" + aDim + "x" + bDim));
        
        String jsonASection = "{\"a\": ";
        stream.println(jsonASection);
        MatrixUtil.finePrint(a, stream);
        
        String jsonBSection = ", \"b\": ";
        stream.println(jsonBSection);
        MatrixUtil.finePrint(b, stream);
    
        String ending = ",\"horizontalBlockNum\": 42,\"verticalBlockNum\": 42}";
        stream.println(ending);
        stream.close();
    }  
    
    @Test
    public void shouldWriteResultBlock() throws Exception {
        int aDim = 1200;
        Integer[][] result = new Integer[aDim][aDim];
        
        Random random = new Random();
        MatrixUtil.randomize(result, random, 10000);
        PrintStream stream = new PrintStream(new FileOutputStream("/tmp/matrixResultBlock" + aDim + "x" + aDim));
        
        String jsonStart = "{\"result\": ";
        stream.println(jsonStart);
        MatrixUtil.finePrint(result, stream);
        
        String ending = ",\"horizontalBlockNum\": 42,\"verticalBlockNum\": 42}";
        stream.println(ending);
        stream.close();
    }
}