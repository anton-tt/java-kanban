package managers;
import tasks.*;
import basic.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, SubTask> subtaskMap = new HashMap<>();
    private final Map<Integer, EpicTask> epicMap = new HashMap<>();
    private final List<Task> allTasksList = new ArrayList<>();
    private final List<Integer> subtasksOneEpicList = new ArrayList<>();
    private final Map<Integer, Status> subtasksOneEpicMap = new HashMap<>();

    private int nextId = 0;
    @Override
    public int getNextId() {
        return nextId++;
    }

    @Override
    public void putNewTaskInMap(Task task) {
        taskMap.put(task.getId(), task);
    }
    @Override
    public void putNewEpictaskInMap(EpicTask epictask) {
        epicMap.put(epictask.getId(), epictask);
    }
    @Override
    public void putNewSubtaskInMap(SubTask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        EpicTask subtaskEpictask = epicMap.get(subtask.getSubtaskEpictaskId());
        updateEpictask(subtaskEpictask);
    }

    @Override
    public List<Task> getListTasks() {
        if(!taskMap.isEmpty()) {
            for (int keyTask : taskMap.keySet()) {
                allTasksList.add(taskMap.get(keyTask));
            }
        }
        if(!subtaskMap.isEmpty()) {
            for (int keyTask : subtaskMap.keySet()) {
                allTasksList.add(subtaskMap.get(keyTask));
            }
        }
        if(!epicMap.isEmpty()) {
            for (int keyTask : epicMap.keySet()) {
                allTasksList.add(epicMap.get(keyTask));
            }
        }
        System.out.println("Проверяем работу метода getListTasks()");
        if(!allTasksList.isEmpty()) {
            System.out.println("Список задач:");
            for (int i = 0; i < allTasksList.size(); i++) {
                Task task = allTasksList.get(i);
                System.out.println("Задача " + task.name);
            }
        } else {
            System.out.println("Список задач вывести невозможно, задачи отсутствуют!");
        }
        return allTasksList;
    }

    @Override
    public void clearMapTasks() {
        if(!taskMap.isEmpty()) {
            taskMap.clear();
        }
        if(!subtaskMap.isEmpty()) {
            subtaskMap.clear();
        }
        if(!epicMap.isEmpty()) {
            epicMap.clear();
        }
        if(!allTasksList.isEmpty()) {
            allTasksList.clear();
        }
    }

    @Override
    public Task getRequiredTask(int id) {
        Task requiredTask = null;
        if(taskMap.containsKey(id)) {
            requiredTask = taskMap.get(id);
        } else if(subtaskMap.containsKey(id)) {
            requiredTask = subtaskMap.get(id);
        } else if(epicMap.containsKey(id)) {
            requiredTask = epicMap.get(id);
        }
        System.out.println("Проверяем работу метода getRequiredTask()");
        if(requiredTask == null) {
            System.out.println("Задача с идентификатором " + id + " отсутствует!");
        } else {
            Managers.getDefaultHistory().addViewedTask(requiredTask);

            System.out.println("Задача с идентификатором " + id + ": " + requiredTask.name);
        }
        return requiredTask;
    }

    public List<Task> getHistory() {
        return Managers.getDefaultHistory().getHistory();
    }


    @Override
    public void removeTask(int id) {
        if(taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else if(subtaskMap.containsKey(id)) {
            SubTask requiredTask = subtaskMap.get(id);
            EpicTask subtaskEpictask = epicMap.get(requiredTask.getSubtaskEpictaskId());
            subtaskMap.remove(id);
            updateEpictask(subtaskEpictask);
        } else if(epicMap.containsKey(id)) {
            EpicTask epicTask = epicMap.get(id);
            List<Integer> epictaskSubtasks = epicTask.getEpictaskSubtasks();
            for(int i = 0; i < epictaskSubtasks.size(); i++) {
                int subtaskId = epictaskSubtasks.get(i);
                subtaskMap.remove(subtaskId);
            }
            epicMap.remove(id);
        }
        System.out.println("Проверяем работу метода removeTask()");
        getRequiredTask(id);
    }

    @Override
    public void updateTask(Task oldTask, Task newTask) {
        taskMap.remove(oldTask.getId());
        putNewTaskInMap(newTask);
    }
    @Override
    public void updateSubtask(SubTask oldTask, SubTask newTask) {
        subtaskMap.remove(oldTask.getId());
        putNewSubtaskInMap(newTask);
    }

    public List<Integer> selectSubtasksOneEpic(EpicTask epictask) {
        subtasksOneEpicList.clear();
        if(!subtaskMap.isEmpty()) {
            for (SubTask subtask : subtaskMap.values()) {
                if (epictask.getId() == subtask.getSubtaskEpictaskId()) {
                    subtasksOneEpicList.add(subtask.getId());
                }
            }
        } else {
            System.out.println("Подзадачи отсутствуют, отсортировать для определённого эпика невозможно!");
        }
        return subtasksOneEpicList;
    }

    public void setSubtasksEpic(EpicTask epictask) {
        List<Integer> epictaskNewSubtasks  = selectSubtasksOneEpic(epictask);
        epictask.setEpictaskSubtasks(epictaskNewSubtasks);
    }

    public Map<Integer, Status> selectSubtasksOneEpicMap(EpicTask epictask) {
        subtasksOneEpicMap.clear();
        List<Integer> subTasks = epictask.getEpictaskSubtasks();
        if (!subTasks.isEmpty()) {
            for(int i = 0; i < subTasks.size(); i++) {
                int subtaskId = subTasks.get(i);
                subtasksOneEpicMap.put(subtaskId, (subtaskMap.get(subtaskId)).status);
            }
        } else {
            System.out.println("Подзадачи у эпика отсутствуют, отсортировать их со статусом невозможно");
        }
        return subtasksOneEpicMap;
    }

    @Override
    public Status generateStatusEpic(EpicTask epictask) {
        selectSubtasksOneEpicMap(epictask);
        Status statusEpic = null;
        boolean statusNew = subtasksOneEpicMap.containsValue(Status.NEW);
        boolean statusProgress = subtasksOneEpicMap.containsValue(Status.IN_PROGRESS);
        boolean statusDone = subtasksOneEpicMap.containsValue(Status.DONE);
        if(statusDone && !statusNew && !statusProgress) {
            statusEpic = Status.DONE;
        } else if((subtasksOneEpicMap.size() == 0 || statusNew) && !statusDone && !statusProgress) {
            statusEpic = Status.NEW;
        } else {
            statusEpic = Status.IN_PROGRESS;
        }
        return statusEpic;
    }

    public void setStatusEpic(EpicTask epictask) {
        Status epictaskNewStatus = generateStatusEpic(epictask);
        epictask.setStatus(epictaskNewStatus);
    }

    @Override
    public void updateEpictask(EpicTask epictask) {
        setSubtasksEpic(epictask);
        setStatusEpic(epictask);
    }

}