package matrix.service.controller;

import matrix.sheduler.ExperimentConductor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        
        return "launched";
    }
    
}