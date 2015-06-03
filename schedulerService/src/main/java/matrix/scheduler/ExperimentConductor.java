package matrix.scheduler;

import concurrent.ObjectPool;
import dto.ExperimentStrategy;
import dto.MatrixMultiplyResultDTO;
import matrix.multiplication.TaskGenerator;
import matrix.multiplication.task.MatrixMultiplyTask;
import matrix.multiplication.task.TaskIndex;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import service.worker.Worker;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ExperimentConductor {
    private static Logger logger = Logger.getLogger(ExperimentConductor.class);
    
    private final BasicTaskScheduler<MatrixMultiplyTask> scheduler;
    
    private final ObjectPool<Worker> workersPool;
    private final ExperimentStrategy strategy;
    private final Random random = new Random();
    private final ConcurrentLinkedQueue<MatrixMultiplyResultDTO> results = new ConcurrentLinkedQueue<>();
    private final Map<TaskIndex, Worker> tasksAssignedToWorkers = new ConcurrentHashMap<>();
    
    private Executor sendersPool;
    private volatile CountDownLatch resultsLatch;
    private final ExperimentTimingInfo timingInfo = new ExperimentTimingInfo();
    
    public ExperimentConductor(BasicTaskScheduler<MatrixMultiplyTask> scheduler, ObjectPool<Worker> workersPool,
                               ExperimentStrategy strategy) {
        this.scheduler = scheduler;
        this.workersPool = workersPool;
        this.strategy = strategy;
        this.workersPool.open();
    }
    
    
    @Value("${scheduler.worker.hostsAndProductivity}")
    private String[] workerHostsAndProductivity;
    
    @Value("${scheduler.worker.publishTaskPath}")
    private String workerPublishTaskPath;
    
    @Value("${scheduler.worker.port}")
    private int workerPort;
    
    @PostConstruct
    private void init() {
        initWorkers();
        sendersPool = Executors.newFixedThreadPool(workerHostsAndProductivity.length);
    }
    
    private void initWorkers() {
        Validate.isTrue(ArrayUtils.isNotEmpty(workerHostsAndProductivity), "no hosts configured");
        Validate.notNull(workerPublishTaskPath, "workerServicePath  null");
        Validate.notNull(workerPort, "no worker port configured");
        Validate.notNull(workerPort, "no worker port configured");
        
        //TODO pivanenko add workers count parameter instead of copy-pasting in properties file
        for (int i = 0; i < workerHostsAndProductivity.length; i++) {
            String[] split = workerHostsAndProductivity[i].split(":");
            String host = split[0];
            Integer productivity = Integer.valueOf(split[1]);
            int num = i + 1;
            String url = composeWorkerUrl(host);
            workersPool.add(new Worker(url, num, productivity));
        }
        
        logger.info("initialised " + workerHostsAndProductivity.length + " workers");
    }
    
    public void handleResult(MatrixMultiplyResultDTO result) {
        TaskIndex index = new TaskIndex(result.getHorizontalBlockNum(), result.getVerticalBlockNum(), result.getClientNumber());
        timingInfo.recordClientTime(result.getClientNumber(), System.currentTimeMillis());
        logger.debug("received result " + index + " for client number " + result.getClientNumber());
        Worker worker = tasksAssignedToWorkers.get(index);
        if (worker == null) {
            logger.error("Can't handle result. Can't find acquired worker for task " + index);
            throw new IllegalStateException("Can't handle result. Index mismatch");
        }
        results.add(result);
        worker.release();
        workersPool.release(worker);
        logger.debug("released worker " + worker.getDescription());
        resultsLatch.countDown();
    }
    
    public synchronized void conductExperiment(Integer matrixDimension, Integer firstClientBlockSize,
                                               Integer secondClientBlockSize) {
        scheduler.clearTasks();
        
        long start = System.currentTimeMillis();
        
        List<MatrixMultiplyTask> tasks = generateTasks(matrixDimension, firstClientBlockSize, 1);
        tasks.addAll(generateTasks(matrixDimension, secondClientBlockSize, 2));
        Collections.shuffle(tasks);
        
        scheduler.submitAll(tasks);
        long generated = System.currentTimeMillis();
        timingInfo.startMeasuring();
        startProcessing();
        waitForResults();
        long processed = System.currentTimeMillis();
        mergeResults(matrixDimension);
        long finish = System.currentTimeMillis();
        
        String experimentInfo = " experiment is for dim=" + matrixDimension + " " +
                "first client blockSize=" + firstClientBlockSize +
                " second client blockSize=" + secondClientBlockSize;
        logger.info("generated tasks in " + (generated - start) + experimentInfo);
        logger.info("processed tasks in " + (processed - generated) + experimentInfo);
        logger.info("merged results  in " + (finish - processed) + experimentInfo);
        logger.info("total calculation time is " + (finish - generated) + experimentInfo + " " + timingInfo
                .getTimingInfo());
        logger.info("total time is " + (finish - start) + experimentInfo);
    }
    
    private String composeWorkerUrl(String host) {
        return "http://" + host + ":" + workerPort + workerPublishTaskPath;
    }
    
    private List<MatrixMultiplyTask> generateTasks(int matrixDimension, int squareSubBlockDimension,
                                                   int clientNumber) {
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
        
        return TaskGenerator.generateMultiplyTasks(a, b, squareSubBlockDimension, clientNumber, strategy);
        
    }
    
    private void startProcessing() {
        resultsLatch = new CountDownLatch(scheduler.tasksCount());
        long start = System.currentTimeMillis();
        logger.debug("Start of tasks distribution. tasksCount " + scheduler.tasksCount());
        
        while (scheduler.hasTasks()) {
            MatrixMultiplyTask task = scheduler.get();
            logger.debug("scheduler gave task client=" + task.getClientNumber()
                    + " index " + task.getIndex() + " complexity is " + task.getComplexity());
            
            CompletableFuture.runAsync(() -> sendTaskForProcessing(task), sendersPool);
        }
        long stop = System.currentTimeMillis();
        logger.debug("all tasks were sent for processing in " + (stop - start) + " millis");
    }
    
    //TODO pivanenko extract task sender
    private void sendTaskForProcessing(MatrixMultiplyTask task) {
        logger.debug("acquiring worker");
        Worker worker = workersPool.acquire();
        tasksAssignedToWorkers.put(new TaskIndex(task.getIndex()), worker);
        logger.debug("acquired worker " + worker.getDescription() + " productivity=" + worker.getProductivityIndex());
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
            throw new RuntimeException("something went wrong");
        }
    }
    
    
    //TODO pivanenko introduce merger
    private Map<Integer, Integer[][]> mergeResults(Integer matrixDimension) {
        Map<Integer, Integer[][]> resultsByClientId = new HashMap<>();
        
        
        while (!results.isEmpty()) {
            MatrixMultiplyResultDTO result = results.poll();
            int clientNumber = result.getClientNumber();
            Integer[][] matrixToWriteResult;
            if (resultsByClientId.containsKey(clientNumber)) {
                matrixToWriteResult = resultsByClientId.get(clientNumber);
            } else {
                matrixToWriteResult = new Integer[matrixDimension][matrixDimension];
                resultsByClientId.put(clientNumber, matrixToWriteResult);
            }
            Validate.noNullElements(result.getResult());
            Integer blockSize = Math.min(result.getResult().length, result.getResult()[0].length);
            
            MatrixUtil.copyBlockToMatrix(matrixToWriteResult, result.getHorizontalBlockNum() * blockSize,
                    result.getVerticalBlockNum() * blockSize,
                    result.getResult());
        }
        return resultsByClientId;
    }
    
    
}
