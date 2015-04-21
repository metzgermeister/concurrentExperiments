package matrix;


import static matrix.ArraysUtil.zero;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.noNullElements;

public class SerialMultiplier {
    
    protected void multiply(MatrixMultiplyTask task) {
        task.setResult(multiplySerial(task.getA(), task.getB()));
        task.markCalculated();
    }
    
    //TODO pivanenko util
    public Integer[][] multiplySerial(Integer[][] a, Integer[][] b) {
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
    
    private void validate(Integer[][] a, Integer[][] b) {
        noNullElements(a);
        noNullElements(b);
        
        int aColumns = a[0].length;
        int bRows = b.length;
        
        isTrue(aColumns == bRows, "A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
    }
    
    
}
