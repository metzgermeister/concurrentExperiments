package matrix.sheduler;

import concurrent.ConcurrentObjectPool;
import concurrent.ObjectPool;
import matrix.multiplication.task.MatrixMultiplyTask;
import matrix.multiplication.SquareMatrixBlockMultiplier;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import service.worker.Worker;

import java.util.List;
import java.util.Random;


@Component
@Scope("singleton")
public class ExperimentConductor {
    private static Logger logger = Logger.getLogger(ExperimentConductor.class);
    
    private final OpportunisticTaskScheduler<MatrixMultiplyTask> scheduler = new OpportunisticTaskScheduler<>(1000);
    private final Random random = new Random();
    
    private final ObjectPool<Worker> workers = new ConcurrentObjectPool<>();
    
    {
        workers.open();
    }
    
    @Value("${scheduler.worker.hosts}")
    private String[] workerHosts;
    
    @Value("${scheduler.worker.publishTaskPath}")
    private String workerPublishTaskPath;
    
    @Value("${scheduler.worker.port}")
    private int workerPort;
    
    public void initWorkers() {
        Validate.isTrue(ArrayUtils.isNotEmpty(workerHosts), "no hosts configured");
        Validate.notNull(workerPublishTaskPath, "workerServicePath  null");
        Validate.notNull(workerPort, "no worker port configured");
        Validate.notNull(workerPort, "no worker port configured");
        
        for (int i = 0; i < workerHosts.length; i++) {
            String host = workerHosts[i];
            int num = i + 1;
            String url = composeWorkerUrl(host);
            workers.add(new Worker(url, num));
        }
        
        logger.info("initialised " + workerHosts.length + " workers");
    }
    
    private String composeWorkerUrl(String host) {
        return "http://" + host + ":" + workerPort + workerPublishTaskPath;
    }
    
    public void generateTasks(int matrixDimension, int squareSubBlockDimension) {
        Validate.isTrue(matrixDimension > 0, "negative matrix dimension passed");
        Validate.isTrue(squareSubBlockDimension > 0, "negative sub-block dimension passed");
        Validate.isTrue(matrixDimension % squareSubBlockDimension == 0, "block size " + squareSubBlockDimension + " " +
                "is not divider of input " + "matrix dimension " + matrixDimension);
        
        Integer[][] a = new Integer[matrixDimension][matrixDimension];
        Integer[][] b = new Integer[matrixDimension][matrixDimension];
        
        MatrixUtil.randomize(a, random, 100);
        MatrixUtil.randomize(b, random, 100);
        
        //TODO pivanenko  refactor SquareMatrixBlockMultiplier to split task generation and processing  
        SquareMatrixBlockMultiplier multiplier = new SquareMatrixBlockMultiplier(squareSubBlockDimension);
        List<MatrixMultiplyTask> matrixMultiplyTasks = multiplier.generateMultiplyTasks(a, b, squareSubBlockDimension);
        scheduler.submitAll(matrixMultiplyTasks);
    }
    
    public void startProcessing() {
        logger.info("Starting processing. tasksCount " + scheduler.tasksCount());
        while (scheduler.hasTasks()) {
            MatrixMultiplyTask matrixMultiplyTask = scheduler.get();
            
            logger.debug("acquiring worker");
            Worker worker = workers.acquire();
            logger.debug("acquired");
            if (logger.isDebugEnabled()) {
                logger.debug("sending task " + matrixMultiplyTask.getIndex() + " to worker " + worker
                        .getDescription());
            }
            
            worker.processTask(matrixMultiplyTask);
        }
        logger.info("all tasks were processed");
    }
    
}
