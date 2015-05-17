package worker.controller;

import dto.MatrixMultiplyTaskDTO;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import worker.processing.Multiplier;

import javax.annotation.Resource;

@RestController
public class WorkerController {
    private static Logger logger = Logger.getLogger(WorkerController.class);
    
    @Resource
    Multiplier multiplier;
    
    @RequestMapping(value = "/publishTask", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResult(@RequestBody MatrixMultiplyTaskDTO task) {
        logger.debug("worker service received task hor=" + task.getHorizontalBlockNum()
                + " vert=" + task.getVerticalBlockNum());
        multiplier.calculateTask(task);
        return "accepted task";
    }
    
    
}