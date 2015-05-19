package matrix.service.controller;

import dto.MatrixMultiplyResultDTO;
import matrix.sheduler.ExperimentConductor;
import matrix.util.MatrixUtil;
import org.apache.log4j.Logger;
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
    
    private static Logger logger = Logger.getLogger(ExperimentController.class);
    
    @Resource
    ExperimentConductor conductor;
    
    @RequestMapping(value = "/startIt/{matrixDimension}/{blockSize}", method = RequestMethod.GET)
    public String trigger(@PathVariable("matrixDimension") Integer matrixDimension,
                          @PathVariable("blockSize") Integer blockSize) {
        long start = System.currentTimeMillis();
        conductor.generateTasks(matrixDimension, blockSize);
        long generated = System.currentTimeMillis();
        conductor.startProcessing();
        
        long processed = System.currentTimeMillis();
        Integer[][] multiplicationResult = conductor.mergeResults(matrixDimension, blockSize);
        long finish = System.currentTimeMillis();
        
        logger.info("generated tasks in " + (generated - start));
        logger.info("processed tasks in " + (processed - generated));
        logger.info("merged results  in " + (finish - processed));
        logger.info("total calculation time is " + (finish - generated));
        logger.info("total time is " + (finish - start));
        MatrixUtil.finePrint(multiplicationResult, System.out);
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