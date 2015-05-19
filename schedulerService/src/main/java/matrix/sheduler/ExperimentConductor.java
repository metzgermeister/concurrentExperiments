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

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


@Component
@Scope("singleton")
public class ExperimentConductor {
    private static Logger logger = Logger.getLogger(ExperimentConductor.class);
    
    private OpportunisticTaskScheduler<MatrixMultiplyTask> scheduler = new OpportunisticTaskScheduler<>();
    private final Random random = new Random();
    private final Map<TaskIndex, Worker> workersToTaskIndex = new ConcurrentHashMap<>();
    
    private final ObjectPool<Worker> workersPool = new ConcurrentObjectPool<>();
    private volatile CountDownLatch resultsLatch;
    
    private ConcurrentLinkedQueue<MatrixMultiplyResultDTO> results = new ConcurrentLinkedQueue<>();
    
    {
        workersPool.open();
    }
    
    @Value("${scheduler.worker.hosts}")
    private String[] workerHosts;
    
    @Value("${scheduler.worker.publishTaskPath}")
    private String workerPublishTaskPath;
    
    @Value("${scheduler.worker.port}")
    private int workerPort;
    
    @PostConstruct
    private void initWorkers() {
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
    
    public void handleResult(MatrixMultiplyResultDTO result) {
        TaskIndex index = new TaskIndex(result.getHorizontalBlockNum(), result.getVerticalBlockNum());
        logger.debug("received result " + index);
        Worker worker = workersToTaskIndex.get(index);
        if (worker == null) {
            logger.error("Can't handle result. Can't find acquired worker for task " + index);
            throw new IllegalStateException("Can't handle result. Index mismatch");
        }
        results.add(result);
        worker.release();
        workersPool.release(worker);
        resultsLatch.countDown();
    }
    
    public synchronized void conductExperiment(Integer matrixDimension, Integer blockSize) {
        long start = System.currentTimeMillis();
        generateTasks(matrixDimension, blockSize);
        long generated = System.currentTimeMillis();
        startProcessing();
        waitForResults();
        long processed = System.currentTimeMillis();
        Integer[][] multiplicationResult = mergeResults(matrixDimension, blockSize);
        long finish = System.currentTimeMillis();
        
        String experimentInfo = " experiment is for dim=" + matrixDimension + " " +
                "blockSize=" + blockSize;
        logger.info("generated tasks in " + (generated - start) + experimentInfo);
        logger.info("processed tasks in " + (processed - generated) + experimentInfo);
        logger.info("merged results  in " + (finish - processed) + experimentInfo);
        logger.info("total calculation time is " + (finish - generated) + experimentInfo);
        logger.info("total time is " + (finish - start) + experimentInfo);
//        MatrixUtil.finePrint(multiplicationResult, System.out);
    }
    
    private String composeWorkerUrl(String host) {
        return "http://" + host + ":" + workerPort + workerPublishTaskPath;
    }
    
    private void generateTasks(int matrixDimension, int squareSubBlockDimension) {
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

//        MatrixUtil.finePrint(a, System.out);
//        MatrixUtil.finePrint(b, System.out);
        
        //TODO pivanenko  refactor SquareMatrixBlockMultiplier to split task generation and processing  
        SquareMatrixBlockMultiplier multiplier = new SquareMatrixBlockMultiplier(squareSubBlockDimension);
        List<MatrixMultiplyTask> matrixMultiplyTasks = multiplier.generateMultiplyTasks(a, b, squareSubBlockDimension);
        scheduler.clearTasks();
        scheduler.submitAll(matrixMultiplyTasks);
    }
    
    private void startProcessing() {
        resultsLatch = new CountDownLatch(scheduler.tasksCount());
        long start = System.currentTimeMillis();
        logger.info("Start of tasks distribution. tasksCount " + scheduler.tasksCount());
        while (scheduler.hasTasks()) {
            MatrixMultiplyTask matrixMultiplyTask = scheduler.get();
            
            processTask(matrixMultiplyTask);
        }
        long stop = System.currentTimeMillis();
        logger.info("all tasks were sent for processing in " + (stop - start) + " millis");
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
    
    private void waitForResults() {
        try {
            resultsLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("smth went wrong");
        }
    }
    
    
    private Integer[][] mergeResults(Integer matrixDimension, Integer blockSize) {
        
        Integer[][] matrix = new Integer[matrixDimension][matrixDimension];
        while (!results.isEmpty()) {
            MatrixMultiplyResultDTO result = results.poll();
            MatrixUtil.copyBlockToMatrix(matrix, result.getHorizontalBlockNum() * blockSize,
                    result.getVerticalBlockNum() * blockSize,
                    result.getResult());
        }
        return matrix;
    }
    
    
}
