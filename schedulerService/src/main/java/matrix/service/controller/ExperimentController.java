package matrix.service.controller;

import dto.MatrixMultiplyResultDTO;
import matrix.sheduler.ExperimentConductor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ExperimentController {
    
    @Resource
    ExperimentConductor conductor;
    
    @RequestMapping(value = "/startIt/{matrixDimension}/{blockSize}", method = RequestMethod.GET)
    public String trigger(@PathVariable("matrixDimension") Integer matrixDimension,
                          @PathVariable("blockSize") Integer blockSize) {
        conductor.generateTasks(matrixDimension, blockSize);
        conductor.initWorkers();
        conductor.startProcessing();
//        conductor.mergeResults();
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