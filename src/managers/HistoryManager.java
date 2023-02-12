package managers;
import tasks.*;
import basic.*;
import java.util.List;

public interface HistoryManager {
    void addViewedTask(Task task);

    List<Task> getHistory();

}