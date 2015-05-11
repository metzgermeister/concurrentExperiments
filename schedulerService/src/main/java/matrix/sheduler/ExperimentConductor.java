package matrix.sheduler;

import matrix.MatrixMultiplyTask;
import matrix.SquareMatrixBlockMultiplier;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


@Component
@Scope("singleton")
public class ExperimentConductor {
    private final OpportunisticTaskScheduler<MatrixMultiplyTask> scheduler = new OpportunisticTaskScheduler<>(1000);
    private final Random random = new Random();
    
    public void generateTasks(int matrixDimension, int squareSubBlockDimension) {
        Validate.isTrue(matrixDimension > 0, "negative matrix dimension passed");
        Validate.isTrue(squareSubBlockDimension > 0, "negative sub-block dimension passed");
        Validate.isTrue(matrixDimension % squareSubBlockDimension == 0, "block size " + squareSubBlockDimension + " " +
                "is not divider of input " + "matrix dimension " + matrixDimension);
        
        Integer[][] a = new Integer[matrixDimension][matrixDimension];
        Integer[][] b = new Integer[matrixDimension][matrixDimension];
        
        MatrixUtil.randomize(a, random, 100);
        MatrixUtil.randomize(b, random, 100);
        
        ////TODO pivanenko  refactor SquareMatrixBlockMultiplier to split task generation and processing  
        SquareMatrixBlockMultiplier multiplier = new SquareMatrixBlockMultiplier(squareSubBlockDimension);
        List<MatrixMultiplyTask> matrixMultiplyTasks = multiplier.generateMultiplyTasks(a, b, squareSubBlockDimension);
        scheduler.submitAll(matrixMultiplyTasks);
    }
    
    public void startProcessing() {
        while (scheduler.hasTasks()) {
            MatrixMultiplyTask matrixMultiplyTask = scheduler.get();
//            workers.
        }
    }
    
    
}
