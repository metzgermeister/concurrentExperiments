package worker;

import dto.MatrixMultiplyTaskDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkerController {
    
    
    @RequestMapping(value = "/publishTask", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String acceptResult(@RequestBody MatrixMultiplyTaskDTO task) {
//        conductor.handleResult(result);
        return "received task";
    }
    
    
}