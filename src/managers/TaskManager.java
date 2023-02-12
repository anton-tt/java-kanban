package managers;

import tasks.*;
import basic.*;
import java.util.List;

public interface TaskManager {
    int getNextId();
    Status generateStatusEpic(EpicTask epictask);

    void putNewTaskInMap(Task task);
    void putNewEpictaskInMap(EpicTask epictask);
    void putNewSubtaskInMap(SubTask subtask);

    List<Task> getListTasks();
    void clearMapTasks();
    void removeTask(int id);
    
    Task getRequiredTask(int id);
    List<Task> getHistory();

    void updateTask(Task oldTask, Task newTask);
    void updateSubtask(SubTask oldTask, SubTask newTask);
    void updateEpictask(EpicTask epictask);

}