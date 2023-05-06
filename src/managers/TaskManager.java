package managers;

import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    int getNextId();

    void setStatusEpic(EpicTask epictask);
    void setStartTimeEpic(EpicTask epictask);
    void setDurationEpic(EpicTask epictask);
    void setEndTimeEpic(EpicTask epictask);

    void putNewTaskInMap(Task task);
    void putNewEpictaskInMap(EpicTask epictask);
    void putNewSubtaskInMap(SubTask subtask);

    void putReconstructedTaskInMap(Task task);
    void putReconstructedEpictaskInMap(EpicTask epictask);
    void putReconstructedSubtaskInMap(SubTask subtask);

    List<Task> getListTasks();
    void clearMapTasks();
    void removeTask(int id);
    
    Task getRequiredTask(int id);
    Task discoverTask(int id);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();

    void updateTask(Task oldTask, Task newTask);
    void updateSubtask(SubTask oldTask, SubTask newTask);
    void updateEpictask(EpicTask epictask);

}