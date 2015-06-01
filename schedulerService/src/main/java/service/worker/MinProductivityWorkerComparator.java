package service.worker;

import java.util.Comparator;

public class MinProductivityWorkerComparator implements Comparator<Worker> {
    @Override
    public int compare(Worker first, Worker second) {
        return first.getProductivityIndex().compareTo(second.getProductivityIndex());
    }
}
