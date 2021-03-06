package service.worker;

import dto.MatrixMultiplyTaskDTO;
import matrix.multiplication.task.MatrixMultiplyTask;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

public class Worker {
    
    private static Logger logger = Logger.getLogger(Worker.class);
    
    private RestTemplate restTemplate = new RestTemplate();
    private final String url;
    private final int number;
    private final int productivityIndex;
    private State state = State.Idle;
    
    public Worker(String url, int number, int productivityIndex) {
        this.url = url;
        this.number = number;
        this.productivityIndex = productivityIndex;
    }
    
    public void processTask(MatrixMultiplyTask task) {
        logger.debug("worker number " + number + " is publishing task to " + url);
        MatrixMultiplyTaskDTO dto = new MatrixMultiplyTaskDTO(task.getA(), task.getB(),
                task.getIndex().getHorizontalBlockNum(),
                task.getIndex().getVerticalBlockNum(),
                task.getClientNumber(), task.getExperimentStrategy());
        restTemplate.postForObject(url, dto, String.class);
        state = State.Busy;
        logger.debug("worker number " + number + " published task and is busy now");
    }
    
    public void release() {
        state = State.Idle;
    }
    
    
    public enum State {
        Idle,
        Busy
    }
    
    public String getDescription() {
        return "url " + url + " number " + number + " " + state;
    }
    
    public Integer getProductivityIndex() {
        return productivityIndex;
    }
}
