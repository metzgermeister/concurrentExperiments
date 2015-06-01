package matrix.scheduler.comparators;

import scheduler.ComplexTask;

import java.util.Comparator;

public class MinMinComplexTaskComparator implements Comparator<ComplexTask> {
    @Override
    public int compare(ComplexTask first, ComplexTask second) {
        if (first.getComplexity().equals(second.getComplexity())) {
            return compareByPriority(first, second);
        } else {
            return first.getComplexity().compareTo(second.getComplexity());
        }
    }
    
    private int compareByPriority(ComplexTask first, ComplexTask second) {
        return -1 * first.getPriority().compareTo(second.getPriority());
    }
}
