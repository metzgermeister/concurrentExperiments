package matrix.multiplication;


import matrix.multiplication.task.MatrixMultiplyTask;

import static matrix.util.MatrixUtil.multiplySerial;

public class SerialMultiplier {
    
    protected void multiply(MatrixMultiplyTask task) {
        task.setResult(multiplySerial(task.getA(), task.getB()));
        task.markCalculated();
    }
    
}
