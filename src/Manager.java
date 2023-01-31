import java.util.Map;
import java.util.HashMap;

public class Manager {
    public Map<Integer, Task> taskMap = new HashMap<>();
    public Map<Integer, SubTask> subtaskMap = new HashMap<>();
    public Map<Integer, EpicTask> epicMap = new HashMap<>();
    public Map<Integer, Task> allTasksMap = new HashMap<>();
    public HashMap<SubTask, Status> subtasksOneEpicMap = new HashMap<>();

    private int nextId = 0;
    public int getNextId() {
        return nextId++;
    }

    public void putNewTaskInMap(Task task) {
        taskMap.put(task.getId(), task);
    }
    public void putNewSubtaskInMap(SubTask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        // меняем статус эпика, которому создали подзадачу
        EpicTask subtaskEpictask = subtask.getSubtaskEpictask();
        setStatusEpic(subtaskEpictask);
        //меняем у этого эпика подзадачи через соотв. поле
        HashMap<SubTask, Status> epictaskNewSubtasks  = selectSubtasksOneEpic(subtaskEpictask);
        subtaskEpictask.setEpictaskSubtasks(epictaskNewSubtasks);
    }
    public void putNewEpictaskInMap(EpicTask epictask) {
        epicMap.put(epictask.getId(), epictask);
        setStatusEpic(epictask);
    }

    public Map getListTasks() {
        if(taskMap != null) {
            for (int keyTask : taskMap.keySet()) {
                allTasksMap.put(keyTask, taskMap.get(keyTask));
            }
        }
        if(subtaskMap != null) {
            for (int keyTask : subtaskMap.keySet()) {
                allTasksMap.put(keyTask, subtaskMap.get(keyTask));
            }
        }
        if(epicMap != null) {
            for (int keyTask : epicMap.keySet()) {
                allTasksMap.put(keyTask, epicMap.get(keyTask));
            }
        }
        // проверяем работу метода, возвращаем результат
        if(allTasksMap.size() != 0) {
            System.out.println("Список задач:");
            for (Task task : allTasksMap.values()) {
                System.out.println(task.name);
            }
        } else {
            System.out.println("Список задач вывести невозможно, задачи отсутствуют!");
        }
        return allTasksMap;
    }

    public void clearMapTasks() {
        if(taskMap.size() != 0) {
            taskMap.clear();
        }
        if(subtaskMap.size() != 0) {
            subtaskMap.clear();
        }
        if(epicMap.size() != 0) {
            epicMap.clear();
        }
        if(allTasksMap.size() != 0) {
            allTasksMap.clear();
        }
    }

    public Task getRequiredTask(int id) {
        Task requiredTask = null;
        if(taskMap.containsKey(id)) {
            requiredTask = taskMap.get(id);
        } else if(subtaskMap.containsKey(id)) {
            requiredTask = subtaskMap.get(id);
        } else if(epicMap.containsKey(id)) {
            requiredTask = epicMap.get(id);
        }
        // проверяем работу метода, возвращаем результат
        if(requiredTask == null) {
            System.out.println("Задача с идентификатором " + id + " отсутствует!");
        } else {
            System.out.println("Задача с идентификатором " + id + ": " + requiredTask.name);
        }
        return requiredTask;
    }

    public void removeTask(int id) {
        if(taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else if(subtaskMap.containsKey(id)) {
            SubTask requiredTask = subtaskMap.get(id);
            EpicTask subtaskEpictask = requiredTask.getSubtaskEpictask();
            subtaskMap.remove(id);
            // изменяем статус и подзадачи эпика, в котором удалили искомую подзадачу
            setStatusEpic(subtaskEpictask);
            setSubtasksEpic(subtaskEpictask);
        } else if(epicMap.containsKey(id)) {
            // сначала удаляем подзадачи эпика, а потом сам эпик
            EpicTask epicTask = epicMap.get(id);
            HashMap<SubTask, Status> epictaskSubtasks = epicTask.getEpictaskSubtasks();
            for(SubTask subTask : epictaskSubtasks.keySet()) {
                subtaskMap.remove(subTask);
            }
            epicMap.remove(id);
        }
        // проверяем работу метода
        getRequiredTask(id);
    }

    public void updateTask(Task oldTask, Task newTask) {
        taskMap.remove(oldTask.getId());
        putNewTaskInMap(newTask);
    }
    public void updateSubtask(SubTask oldTask, SubTask newTask) {
        putNewSubtaskInMap(newTask);
        removeTask(oldTask.getId());
    }
    public void updateEpictask(EpicTask oldTask, EpicTask newTask) {
        removeTask(oldTask.getId());
        putNewEpictaskInMap(newTask);
    }

    public HashMap<SubTask, Status> selectSubtasksOneEpic(EpicTask epictask) {
        subtasksOneEpicMap.clear();
        if(subtaskMap.size() != 0) {
            for (SubTask subtask : subtaskMap.values()) {
                if (epictask.getId() == (subtask.getSubtaskEpictask()).getId()) {
                    subtasksOneEpicMap.put(subtask, subtask.status);
                }
            }
        } else {
            System.out.println("Подзадачи отсутствуют, отсортировать для определённого эпика невозможно!");
        }
        return subtasksOneEpicMap;
    }

    public Status generateStatusEpic(EpicTask epictask) {
        selectSubtasksOneEpic(epictask);
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

    public void setSubtasksEpic(EpicTask epictask) {
        HashMap<SubTask, Status> epictaskNewSubtasks  = selectSubtasksOneEpic(epictask);
        epictask.setEpictaskSubtasks(epictaskNewSubtasks);
    }

}