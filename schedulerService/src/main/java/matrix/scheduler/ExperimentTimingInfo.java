package matrix.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ExperimentTimingInfo {
    
    private final AtomicLong experimentStartTime = new AtomicLong();
    private final Map<Integer, Long> lastCalculatedTaskTimeByUser = new ConcurrentHashMap<>();
    
    public long getExperimentStartTime() {
        return experimentStartTime.get();
    }
    
    public void setStartTime(long time) {
        experimentStartTime.set(time);
    }
    
    public void startMeasuring() {
        synchronized (this) {
            lastCalculatedTaskTimeByUser.clear();
            experimentStartTime.set(System.currentTimeMillis());
        }
    }
    
    
    public void recordClientTime(int clientCUmber, long millis) {
        lastCalculatedTaskTimeByUser.put(clientCUmber, millis);
    }
    
    public String getTimingInfo() {
        synchronized (this) {
            StringBuilder timingInfo = new StringBuilder();
            for (Map.Entry<Integer, Long> clientTime : lastCalculatedTaskTimeByUser.entrySet()) {
                timingInfo.append(" client ");
                timingInfo.append(clientTime.getKey());
                timingInfo.append(" time ");
                timingInfo.append(clientTime.getValue() - experimentStartTime.get());
            }
            return timingInfo.toString();
        }
    }
    
}
