package managers;

import tasks.*;
import java.util.List;

public interface HistoryManager {
    void addViewedTask(Task task);

    List<Task> getHistory();

    void removeRecurringTask(int id);

}