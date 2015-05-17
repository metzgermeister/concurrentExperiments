package matrix.sheduler;

import concurrent.ConcurrentObjectPool;
import concurrent.ObjectPool;
import dto.MatrixMultiplyResultDTO;
import matrix.multiplication.SquareMatrixBlockMultiplier;
import matrix.multiplication.task.MatrixMultiplyTask;
import matrix.multiplication.task.TaskIndex;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import service.worker.Worker;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Scope("singleton")
public class ExperimentConductor {
    private static Logger logger = Logger.getLogger(ExperimentConductor.class);
    
    private final OpportunisticTaskScheduler<MatrixMultiplyTask> scheduler = new OpportunisticTaskScheduler<>(1000);
    private final Random random = new Random();
    private final Map<TaskIndex, Worker> workersToTaskIndex = new ConcurrentHashMap<>();
    
    private final ObjectPool<Worker> workersPool = new ConcurrentObjectPool<>();
    
    {
        workersPool.open();
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
            workersPool.add(new Worker(url, num));
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
        logger.debug("experiment for square matrices dimension=" + matrixDimension + "  and block size is " +
                squareSubBlockDimension);
        
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
        long start = System.currentTimeMillis();
        logger.info("Starting processing. tasksCount " + scheduler.tasksCount());
        while (scheduler.hasTasks()) {
            MatrixMultiplyTask matrixMultiplyTask = scheduler.get();
            
            processTask(matrixMultiplyTask);
        }
        long stop = System.currentTimeMillis();
        logger.info("all tasks were processed in " + (stop - start) + " millis");
    }
    
    private void processTask(MatrixMultiplyTask task) {
        logger.debug("acquiring worker");
        Worker worker = workersPool.acquire();
        workersToTaskIndex.put(new TaskIndex(task.getIndex()), worker);
        logger.debug("acquired");
        if (logger.isDebugEnabled()) {
            logger.debug("sending task " + task.getIndex() + " to worker " + worker
                    .getDescription());
        }
        
        worker.processTask(task);
    }
    
    public void handleResult(MatrixMultiplyResultDTO result) {
        TaskIndex index = new TaskIndex(result.getHorizontalBlockNum(), result.getVerticalBlockNum());
        logger.debug("received result " + index);
        Worker worker = workersToTaskIndex.get(index);
        if (worker == null) {
            logger.error("Can't handle result. Can't find acquired worker for task " + index);
            throw new IllegalStateException("Can't handle result. Index mismatch");
        }
        //TODO pivanenko collect result
        worker.release();
        workersPool.release(worker);
    }
}
