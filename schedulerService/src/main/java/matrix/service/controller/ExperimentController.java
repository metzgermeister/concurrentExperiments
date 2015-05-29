package matrix.service.controller;

import dto.MatrixMultiplyResultDTO;
import matrix.sheduler.ExperimentConductor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ExperimentController {
    
    private static Logger logger = Logger.getLogger(ExperimentController.class);
    
    @Resource
    ExperimentConductor conductor;
    
//    @RequestMapping(value = "/startIt/{matrixDimension}/{blockSize}", method = RequestMethod.GET)
//    public String trigger(@PathVariable("matrixDimension") Integer matrixDimension,
//                          @PathVariable("blockSize") Integer blockSize) {
//        conductor.conductExperiment(matrixDimension, blockSize);
//        return "finished, look at logs";
//    }
    
    
    @RequestMapping(value = "/twoClientsExperiment/{matrixDimension}/", method = RequestMethod.GET)
    public String twoClientsExperiment(@PathVariable("matrixDimension") Integer matrixDimension,
                                       @RequestParam("firstClientBlockSize") Integer firstClientBlockSize,
                                       @RequestParam("firstClientBlockSize") Integer secondClientBlockSize) {
        conductor.conductExperiment(matrixDimension, firstClientBlockSize, secondClientBlockSize);
        return "finished, look at logs";
    }
    
    @RequestMapping(value = "/publishResult", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResult(@RequestBody MatrixMultiplyResultDTO result) {
        conductor.handleResult(result);
        return "Got it";
    }
    
    @RequestMapping(value = "/publishResultMock", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResultMock(@RequestBody MatrixMultiplyResultDTO result) {
        logger.debug("scheduler service received result hor=" + result.getHorizontalBlockNum()
                + " vert=" + result.getVerticalBlockNum());
        return "Got it";
    }
    
}