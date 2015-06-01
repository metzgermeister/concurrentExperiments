package matrix.service.controller;

import dto.ExperimentStrategy;
import dto.MatrixMultiplyResultDTO;
import matrix.scheduler.ExperimentConductor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
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
    
    public static final String FINISHED_LOOK_AT_LOGS = "finished, look at logs";
    private static Logger logger = Logger.getLogger(ExperimentController.class);
    
    @Resource(name = "minMinConductor")
    ExperimentConductor minMinConductor;
    
    
    @Resource(name = "minMaxConductor")
    ExperimentConductor minMaxConductor;
    
    @Resource(name = "maxMaxConductor")
    ExperimentConductor maxMaxConductor;
    
    
    @Resource(name = "maxMinConductor")
    ExperimentConductor maxMinConductor;
    
    
    @RequestMapping(value = "/twoClientsExperiment/minMin/{matrixDimension}", method = RequestMethod.GET)
    public String twoClientsMinMinExperiment(@PathVariable("matrixDimension") Integer matrixDimension,
                                             @RequestParam("firstClientBlockSize") Integer firstClientBlockSize,
                                             @RequestParam("secondClientBlockSize") Integer secondClientBlockSize) {
        validateBlockSizes(firstClientBlockSize, secondClientBlockSize);
        minMinConductor.conductExperiment(matrixDimension, firstClientBlockSize, secondClientBlockSize);
        return FINISHED_LOOK_AT_LOGS;
    }
    
    @RequestMapping(value = "/twoClientsExperiment/minMax/{matrixDimension}", method = RequestMethod.GET)
    public String twoClientsMinMaxExperiment(@PathVariable("matrixDimension") Integer matrixDimension,
                                             @RequestParam("firstClientBlockSize") Integer firstClientBlockSize,
                                             @RequestParam("secondClientBlockSize") Integer secondClientBlockSize) {
        validateBlockSizes(firstClientBlockSize, secondClientBlockSize);
        minMaxConductor.conductExperiment(matrixDimension, firstClientBlockSize, secondClientBlockSize);
        return FINISHED_LOOK_AT_LOGS;
    }
    
    @RequestMapping(value = "/twoClientsExperiment/MaxMax/{matrixDimension}", method = RequestMethod.GET)
    public String twoClientsMaxMaxExperiment(@PathVariable("matrixDimension") Integer matrixDimension,
                                             @RequestParam("firstClientBlockSize") Integer firstClientBlockSize,
                                             @RequestParam("secondClientBlockSize") Integer secondClientBlockSize) {
        validateBlockSizes(firstClientBlockSize, secondClientBlockSize);
        maxMaxConductor.conductExperiment(matrixDimension, firstClientBlockSize, secondClientBlockSize);
        return FINISHED_LOOK_AT_LOGS;
    }
    
    @RequestMapping(value = "/twoClientsExperiment/MaxMin/{matrixDimension}", method = RequestMethod.GET)
    public String twoClientsMaxMinExperiment(@PathVariable("matrixDimension") Integer matrixDimension,
                                             @RequestParam("firstClientBlockSize") Integer firstClientBlockSize,
                                             @RequestParam("secondClientBlockSize") Integer secondClientBlockSize) {
        validateBlockSizes(firstClientBlockSize, secondClientBlockSize);
        maxMinConductor.conductExperiment(matrixDimension, firstClientBlockSize, secondClientBlockSize);
        return FINISHED_LOOK_AT_LOGS;
    }
    
    
    @RequestMapping(value = "/publishResult", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResult(@RequestBody MatrixMultiplyResultDTO result) {
        chooseConductor(result.getStrategy()).handleResult(result);
        return "Got it";
    }
    
    private ExperimentConductor chooseConductor(ExperimentStrategy strategy) {
        switch (strategy) {
            case MIN_MIN:
                return minMinConductor;
            case MIN_MAX:
                return minMaxConductor;
            case MAX_MAX:
                return maxMaxConductor;
            case MAX_MIN:
                return maxMinConductor;
            default:
                throw new NotImplementedException("Strategy " + strategy + " not implemented yet");
        }
        
    }
    
    @RequestMapping(value = "/publishResultMock", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResultMock(@RequestBody MatrixMultiplyResultDTO result) {
        logger.debug("scheduler service received result hor=" + result.getHorizontalBlockNum()
                + " vert=" + result.getVerticalBlockNum());
        return "Got it";
    }
    
    private void validateBlockSizes(@RequestParam("firstClientBlockSize") Integer firstClientBlockSize, @RequestParam("secondClientBlockSize") Integer secondClientBlockSize) {
        Validate.notNull(firstClientBlockSize, "missing firstClientBlockSize parameter");
        Validate.notNull(secondClientBlockSize, "missing secondClientBlockSize parameter");
    }
    
}