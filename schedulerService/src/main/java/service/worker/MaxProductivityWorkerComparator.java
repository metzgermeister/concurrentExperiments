package service.worker;

import java.util.Comparator;

public class MaxProductivityWorkerComparator implements Comparator<Worker> {
    @Override
    public int compare(Worker first, Worker second) {
        return  -1  * first.getProductivityIndex().compareTo(second.getProductivityIndex());
    }
}
