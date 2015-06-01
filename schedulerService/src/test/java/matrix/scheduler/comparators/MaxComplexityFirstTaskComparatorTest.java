package matrix.scheduler.comparators;

import org.junit.Assert;
import org.junit.Test;
import scheduler.ComplexTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaxComplexityFirstTaskComparatorTest {
    
    private final MaxComplexityFirstTaskComparator comparator = new MaxComplexityFirstTaskComparator();
    
    class ComplexTaskImpl implements ComplexTask {
        private final Integer priority;
        private final Integer complexity;
        
        ComplexTaskImpl(Integer priority, Integer complexity) {
            this.priority = priority;
            this.complexity = complexity;
        }
        
        @Override
        public Integer getComplexity() {
            return complexity;
        }
        
        @Override
        public Integer getPriority() {
            return priority;
        }
    }
    
    
    @Test
    public void shouldCompareByComplexity() throws Exception {
        ComplexTaskImpl smallTask = new ComplexTaskImpl(1, 1);
        ComplexTaskImpl bigTask = new ComplexTaskImpl(1, 10);
        List<ComplexTask> tasks = new ArrayList<>();
        tasks.add(bigTask);
        tasks.add(smallTask);
        
        Collections.sort(tasks, comparator);
    
        Assert.assertSame(bigTask, tasks.get(0));
        Assert.assertSame(smallTask, tasks.get(1));
    }
    
    
    @Test
    public void shouldCompareByPriority() throws Exception {
        int complexity = 42;
        ComplexTaskImpl normalTask = new ComplexTaskImpl(1, complexity);
        ComplexTaskImpl highPriority = new ComplexTaskImpl(2, complexity);
        List<ComplexTask> tasks = new ArrayList<>();
        tasks.add(highPriority);
        tasks.add(normalTask);
        
        Collections.sort(tasks, comparator);
        
        Assert.assertSame(highPriority, tasks.get(0));
        Assert.assertSame(normalTask, tasks.get(1));
    }
    
    @Test
    public void shouldIgnorePriorityWhenComplexityIsDifferent() throws Exception {
        ComplexTaskImpl smallLowPriorityTask = new ComplexTaskImpl(1, 1);
        ComplexTaskImpl bigHighPriorityTask = new ComplexTaskImpl(42, 10);
        List<ComplexTask> tasks = new ArrayList<>();
        tasks.add(smallLowPriorityTask);
        tasks.add(bigHighPriorityTask);
        
        Collections.sort(tasks, comparator);
        
        Assert.assertSame(smallLowPriorityTask, tasks.get(1));
        Assert.assertSame(bigHighPriorityTask, tasks.get(0));
    }
    
}