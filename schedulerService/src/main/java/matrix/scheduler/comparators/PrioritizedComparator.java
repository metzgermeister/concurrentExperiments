package matrix.scheduler.comparators;

import scheduler.ComplexTask;

import java.util.Comparator;

public abstract class PrioritizedComparator implements Comparator<ComplexTask> {
    protected int compareByPriority(ComplexTask first, ComplexTask second) {
        return -1 * first.getPriority().compareTo(second.getPriority());
    }
}
