package service.worker;

import matrix.MatrixMultiplyTask;

public class Worker {
    
    public void processTask(MatrixMultiplyTask matrixMultiplyTask) {
        //TODO pivanenko implement asynchronous processing
    }
    
    public enum State {
        Idle,
        Busy
    }
    
    private final String host;
    private final int number;
    
    private State state = State.Idle;
    
    public Worker(String host, int number) {
        this.host = host;
        this.number = number;
    }
    
    
    public String getDescription() {
        return host + " " + number;
    }
    
}
