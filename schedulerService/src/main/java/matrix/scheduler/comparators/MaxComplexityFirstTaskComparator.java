package matrix.scheduler.comparators;

import scheduler.ComplexTask;

public class MaxComplexityFirstTaskComparator extends PrioritizedComparator {
    @Override
    public int compare(ComplexTask first, ComplexTask second) {
        if (first.getComplexity().equals(second.getComplexity())) {
            return compareByPriority(first, second);
        } else {
            return -1 * first.getComplexity().compareTo(second.getComplexity());
        }
    }
    
}
