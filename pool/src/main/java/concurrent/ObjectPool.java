package concurrent;

import java.util.concurrent.TimeUnit;

public interface ObjectPool<R> {
    void open();
    
    boolean isOpen();
    
    void close();
    
    void closeNow();
    
    R acquire();
    
    R acquire(long timeout, TimeUnit timeUnit);
    
    void release(R resource);
    
    boolean add(R resource);
    
    boolean remove(R resource);
    
    boolean removeNow(R resource);
    
}
