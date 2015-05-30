package worker.processing;

import dto.MatrixMultiplyResultDTO;
import dto.MatrixMultiplyTaskDTO;
import matrix.multiplication.task.TaskIndex;
import matrix.util.MatrixUtil;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Scope("singleton")
public class Multiplier {
    private static Logger logger = Logger.getLogger(Multiplier.class);
    
    @Value("${worker.threadCount}")
    private int threadCount;
    
    @Value("${scheduler.publishResultUrl}")
    private String schedulerPublishResultUrl;
    
    private ThreadPoolExecutor multiplicationExecutor;
    private ThreadPoolExecutor resultsPublishExecutor;
    private RestTemplate restTemplate = new RestTemplate();
    
    
    @PostConstruct
    private void initialize() {
        Validate.notNull(threadCount, "thread count not set");
        multiplicationExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        resultsPublishExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        logger.info("initialised multiplicationExecutor pool with threadCount=" + threadCount);
    }
    
    public void calculateTask(MatrixMultiplyTaskDTO task) {
        final TaskIndex index = new TaskIndex(task.getHorizontalBlockNum(), task.getVerticalBlockNum(), task.getClientNumber());
        final CompletableFuture<Integer[][]> future =
                CompletableFuture.supplyAsync(() -> multiplyWithLogging(task), multiplicationExecutor);
        
        future.thenAcceptAsync(matrix -> {
            logger.debug("sending result to scheduler task index is " + index);
            MatrixMultiplyResultDTO resultDTO = new MatrixMultiplyResultDTO(matrix, task.getHorizontalBlockNum(),
                    task.getVerticalBlockNum(), task.getClientNumber());
            restTemplate.postForObject(schedulerPublishResultUrl, resultDTO, String.class);
            logger.debug("sent result of task task " + index);
        }, resultsPublishExecutor);
    }
    
    
    private Integer[][] multiplyWithLogging(MatrixMultiplyTaskDTO task) {
        long start = System.currentTimeMillis();
        Integer[][] result = MatrixUtil.multiplySerial(task.getA(), task.getB());
        long stop = System.currentTimeMillis();
        logger.debug("multiplied task " + task.getA().length + "x" + task.getA()[0].length
                + " " + task.getB().length + "x" + task.getB()[0].length
                + " in " + (stop - start) + " millis");
        return result;
    }
}
