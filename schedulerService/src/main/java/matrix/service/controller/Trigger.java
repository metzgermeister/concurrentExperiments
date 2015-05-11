package matrix.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import matrix.sheduler.ExperimentConductor;

import javax.annotation.Resource;

@RestController
public class Trigger {
    
    @Resource
    ExperimentConductor container;
    
    @RequestMapping(value = "/startIt", method = RequestMethod.GET)
    public String trigger() {
        container.generateTasks(40,20);
    
        return "launched";
    }
}