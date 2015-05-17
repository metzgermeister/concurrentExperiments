package matrix.service.controller;

import dto.MatrixMultiplyResultDTO;
import matrix.sheduler.ExperimentConductor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class Trigger {
    
    @Resource
    ExperimentConductor conductor;
    
    @RequestMapping(value = "/startIt", method = RequestMethod.GET)
    public String trigger() {
        conductor.generateTasks(40, 20);
        conductor.initWorkers();
        conductor.startProcessing();
        return "finished, look at logs";
    }
    
    @RequestMapping(value = "/publishResult", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResult(@RequestBody MatrixMultiplyResultDTO result) {
        conductor.handleResult(result);
        return "Got it";
    }
    
}