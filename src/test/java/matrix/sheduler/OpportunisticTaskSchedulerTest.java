package matrix.sheduler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpportunisticTaskSchedulerTest {
    @Test
    public void shouldKeepOrder() throws Exception {
        OpportunisticTaskScheduler<String> scheduler = new OpportunisticTaskScheduler<>(42);
        
        String first = "first";
        String second = "second";
        scheduler.submit(first);
        scheduler.submit(second);
        
        assertEquals(first, scheduler.get());
        
        String third = "third";
        scheduler.submit(third);
        
        assertEquals(second, scheduler.get());
        assertEquals(third, scheduler.get());
        
    }
}