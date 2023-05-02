package managers;

import tasks.*;
import basic.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, SubTask> subtaskMap = new HashMap<>();
    protected final Map<Integer, EpicTask> epicMap = new HashMap<>();
    Set<Task> prioritizedSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    List<Task> notPrioritizedList = new ArrayList<>();

    private int nextId = 1;

    @Override
    public int getNextId() {
        return nextId++;
    }

    @Override
    public LocalDateTime generateStartTimeTask(int year, int month, int day, int hour, int minute) {
        LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, minute);
        return startTime;
    }

    @Override
    public Duration generateDurationTask(long minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        return duration;
    }

    public LocalDateTime generateEndTimeTask(Task task) {
        LocalDateTime endTime = task.getEndTime();
        if (endTime == null) {
            LocalDateTime startTime = task.getStartTime();
            Duration duration = task.getDuration();
            endTime = startTime.plus(duration);
        }
        return endTime;
    }

    @Override
    public void putNewTaskInMap(Task task) {
        LocalDateTime endTime = generateEndTimeTask(task);
        task.setEndTime(endTime);
        if (sortPriorityTasks(task)) {
            task.setId(getNextId());
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void putNewEpictaskInMap(EpicTask epictask) {
        if (sortPriorityTasks(epictask)) {
            epictask.setId(getNextId());
            epicMap.put(epictask.getId(), epictask);
        }
    }

    @Override
    public void putNewSubtaskInMap(SubTask subtask) {
        LocalDateTime endTime = generateEndTimeTask(subtask);
        subtask.setEndTime(endTime);
        if (sortPriorityTasks(subtask)) {
            subtask.setId(getNextId());
            subtaskMap.put(subtask.getId(), subtask);
            EpicTask subtaskEpictask = epicMap.get(subtask.getSubtaskEpictaskId());
            if(notPrioritizedList.contains(subtaskEpictask)) {
                removePrioritizedTask(subtaskEpictask);
            }
            updateEpictask(subtaskEpictask);
        }
    }

    @Override
    public void putReconstructedTaskInMap(Task task) {
        LocalDateTime endTime = generateEndTimeTask(task);
        task.setEndTime(endTime);
            taskMap.put(task.getId(), task);
    }

    @Override
    public void putReconstructedEpictaskInMap(EpicTask epictask) {
            epicMap.put(epictask.getId(), epictask);
    }

    @Override
    public void putReconstructedSubtaskInMap(SubTask subtask) {
        LocalDateTime endTime = generateEndTimeTask(subtask);
        subtask.setEndTime(endTime);
            subtaskMap.put(subtask.getId(), subtask);
            EpicTask subtaskEpictask = epicMap.get(subtask.getSubtaskEpictaskId());
            if(notPrioritizedList.contains(subtaskEpictask)) {
                removePrioritizedTask(subtaskEpictask);
            }
            updateEpictask(subtaskEpictask);
        }

    @Override
    public List<Task> getListTasks() {
        List<Task> allTasksList = new ArrayList<>();
        if (!taskMap.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
                allTasksList.add(entry.getValue());
            }
        }
        if (!subtaskMap.isEmpty()) {
            for (Map.Entry<Integer, SubTask> entry : subtaskMap.entrySet()) {
                allTasksList.add(entry.getValue());
            }
        }
        if (!epicMap.isEmpty()) {
            for (Map.Entry<Integer, EpicTask> entry : epicMap.entrySet()) {
                allTasksList.add(entry.getValue());
            }
        }
        return allTasksList;
    }

    @Override
    public void clearMapTasks() {
        if (!taskMap.isEmpty()) {
            taskMap.clear();
        }
        if (!subtaskMap.isEmpty()) {
            subtaskMap.clear();
        }
        if (!epicMap.isEmpty()) {
            epicMap.clear();
        }
    }

    @Override
    public Task getRequiredTask(int id) {
        Task requiredTask = null;
        if (taskMap.containsKey(id)) {
            requiredTask = taskMap.get(id);
        } else if (subtaskMap.containsKey(id)) {
            requiredTask = subtaskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            requiredTask = epicMap.get(id);
        }
        if (requiredTask != null) {
            historyManager.addViewedTask(requiredTask);
            System.out.println("Задача с ID " + id + " есть");
        }
        else  {
            System.out.println("Задача с ID " + id + " отсутствует");
        }
        return requiredTask;
    }

    @Override
    public Task discoverTask(int id) {
        Task requiredTask = null;
        if (taskMap.containsKey(id)) {
            requiredTask = taskMap.get(id);
        } else if (subtaskMap.containsKey(id)) {
            requiredTask = subtaskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            requiredTask = epicMap.get(id);
        }
        return requiredTask;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeTask(int id) {
        if (taskMap.containsKey(id)) {
            Task removeTask = taskMap.get(id);
            removePrioritizedTask(removeTask);
            taskMap.remove(id);
        } else if (subtaskMap.containsKey(id)) {
            SubTask removeSubtask = subtaskMap.get(id);
            EpicTask subtaskEpictask = epicMap.get(removeSubtask.getSubtaskEpictaskId());
            subtaskMap.remove(id);
            if(subtaskEpictask.getEpictaskSubtasks().isEmpty()) {
                notPrioritizedList.add(subtaskEpictask);
            }
            updateEpictask(subtaskEpictask);
        } else if (epicMap.containsKey(id)) {
            EpicTask epicTask = epicMap.get(id);
            List<Integer> epictaskSubtasks = epicTask.getEpictaskSubtasks();
            for (int i = 0; i < epictaskSubtasks.size(); i++) {
                int subtaskId = epictaskSubtasks.get(i);
                Task removeSubtask  = subtaskMap.get(subtaskId);
                removePrioritizedTask(removeSubtask);
                subtaskMap.remove(subtaskId);
            }
            removePrioritizedTask(epicTask);
            epicMap.remove(id);
        }
    }

    public void removePrioritizedTask(Task task) {
        if (!notPrioritizedList.isEmpty() && notPrioritizedList.contains(task)) {
            notPrioritizedList.remove(task);
        } else if (!prioritizedSet.isEmpty() && prioritizedSet.contains(task)) {
            prioritizedSet.remove(task);
        }
    }

    @Override
    public void updateTask(Task oldTask, Task newTask) {
        taskMap.remove(oldTask.getId());
        removePrioritizedTask(oldTask);
        putNewTaskInMap(newTask);
    }

    @Override
    public void updateSubtask(SubTask oldTask, SubTask newTask) {
        subtaskMap.remove(oldTask.getId());
        removePrioritizedTask(oldTask);
        putNewSubtaskInMap(newTask);
    }

    @Override
    public void updateEpictask(EpicTask epictask) {
        setSubtasksEpic(epictask);
        setStatusEpic(epictask);
        setStartTimeEpic(epictask);
        setDurationEpic(epictask);
        setEndTimeEpic(epictask);
        sortPriorityTasks(epictask);
    }


    public Map<Integer, SubTask> selectSubtasksOneEpic(EpicTask epictask) {
        int epictaskId = epictask.getId();
        Map<Integer, SubTask> subtasksOneEpic = new HashMap<>();
        if (!subtaskMap.isEmpty()) {
            for (Map.Entry<Integer, SubTask> entry : subtaskMap.entrySet()) {
                int subtaskEpictaskId = (entry.getValue()).getSubtaskEpictaskId();
                if (epictaskId == subtaskEpictaskId) {
                    subtasksOneEpic.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return subtasksOneEpic;
    }

    public List<Integer> generateSubtasksOneEpic(EpicTask epictask) {
        Map<Integer, SubTask> subtasksOneEpic = selectSubtasksOneEpic(epictask);
        List<Integer> subtasksList = new ArrayList<>();
        if (!subtasksOneEpic.isEmpty()) {
            subtasksList.addAll(subtasksOneEpic.keySet());
        }
        return subtasksList;
    }

    public void setSubtasksEpic(EpicTask epictask) {
        List<Integer> epictaskNewSubtasks = generateSubtasksOneEpic(epictask);
        epictask.setEpictaskSubtasks(epictaskNewSubtasks);
    }

    public Status generateStatusEpic(EpicTask epictask) {
        Status statusEpic = null;
        Map<Integer, SubTask> subtasksOneEpic = selectSubtasksOneEpic(epictask);
        Map<Integer, Status> subtasksStatus = new HashMap<>();
        if (!subtasksOneEpic.isEmpty()) {
            for (Map.Entry<Integer, SubTask> entry : subtasksOneEpic.entrySet()) {
                subtasksStatus.put(entry.getKey(), (entry.getValue()).status);
            }
        }
        boolean statusNew = subtasksStatus.containsValue(Status.NEW);
        boolean statusProgress = subtasksStatus.containsValue(Status.IN_PROGRESS);
        boolean statusDone = subtasksStatus.containsValue(Status.DONE);
        if (statusDone && !statusNew && !statusProgress) {
            statusEpic = Status.DONE;
        } else if ((subtasksStatus.size() == 0 || statusNew) && !statusDone && !statusProgress) {
            statusEpic = Status.NEW;
        } else {
            statusEpic = Status.IN_PROGRESS;
        }
        return statusEpic;
    }

    @Override
    public void setStatusEpic(EpicTask epictask) {
        Status epictaskNewStatus = generateStatusEpic(epictask);
        epictask.setStatus(epictaskNewStatus);
    }

    public LocalDateTime generateStartTimeEpic(EpicTask epictask) {
        LocalDateTime startTime = null;
        Map<Integer, SubTask> subtasksOneEpic = selectSubtasksOneEpic(epictask);
        if (!subtasksOneEpic.isEmpty()) {
            for (SubTask task : subtasksOneEpic.values()) {
                LocalDateTime time = task.getStartTime();
                if (startTime == null || startTime.isAfter(time)) {
                    startTime = time;
                }
            }
        }
        return startTime;
    }
    @Override
    public void setStartTimeEpic(EpicTask epictask) {
        LocalDateTime newEpictaskStartTime = generateStartTimeEpic(epictask);
        epictask.setStartTime(newEpictaskStartTime);
    }

    public Duration generateDurationEpic(EpicTask epictask) {
        Duration durationEpic = null;
        Map<Integer, SubTask> subtasksOneEpic = selectSubtasksOneEpic(epictask);
        if (!subtasksOneEpic.isEmpty()) {
            for (SubTask task : subtasksOneEpic.values()) {
                Duration durationSubtask = task.getDuration();
                if (durationEpic == null) {
                    durationEpic = durationSubtask;
                } else {
                    durationEpic = durationEpic.plus(durationSubtask);
                }
            }
        }
        return durationEpic;
    }
    @Override
    public void setDurationEpic(EpicTask epictask) {
        Duration newEpictaskDuration = generateDurationEpic(epictask);
        epictask.setDuration(newEpictaskDuration);
    }

    public LocalDateTime generateEndTimeEpic(EpicTask epictask) {
        LocalDateTime endTime = null;
        Map<Integer, SubTask> subtasksOneEpic = selectSubtasksOneEpic(epictask);
        if (!subtasksOneEpic.isEmpty()) {
            for (SubTask task : subtasksOneEpic.values()) {
                LocalDateTime time = task.getEndTime();
                if (endTime == null || endTime.isBefore(time)) {
                    endTime = time;
                }
            }
        }
        return endTime;
    }

    @Override
    public void setEndTimeEpic(EpicTask epictask) {
        LocalDateTime newEpictaskEndTime = generateEndTimeEpic(epictask);
        epictask.setEndTime(newEpictaskEndTime);
    }

    public boolean sortPriorityTasks(Task newTask) {
        boolean marker = false;
        LocalDateTime newTaskStartTime = newTask.getStartTime();

        if (!prioritizedSet.isEmpty() && newTaskStartTime != null) {
            for (Task task : prioritizedSet) {
                if (checkEpicRelationSubtask(newTask, task)) {
                    System.out.println("Данная задача - эпик, имеющий подзадачи. Он не учитывается при формировании списка приоритетов!");
                    marker = true;
                    return marker;

                }
                if (!checkIntersectionTasks(newTask, task)) {
                    continue;
                }
                System.out.println("Срок исполнения новой задачи частично или полностью совпадает со сроком ранее созданной задачи!");
                System.out.println("Задача не создана! Необходимо изменить срок исполнения новой задачи!");
                return marker;
            }
            prioritizedSet.add(newTask);
        } else if (prioritizedSet.isEmpty() && newTaskStartTime != null) {
            prioritizedSet.add(newTask);

        } else if (newTaskStartTime == null) {
            notPrioritizedList.add(newTask);
        }
        marker = true;
        getPrioritizedTasks();
        return marker;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> tasksList = new ArrayList<>();
        tasksList.addAll(prioritizedSet);
        tasksList.addAll(notPrioritizedList);
        /*for (Task task : tasksList) {
            System.out.println("Задачи по приоритету");
            System.out.println("Задача " + task.getId() + " название " + task.name + " начало " + task.getStartTime());
        }*/
        return tasksList;
    }

    public boolean checkEpicRelationSubtask(Task newTask, Task oldTask) {
        boolean marker = false;
        if ((newTask instanceof EpicTask) && (oldTask instanceof SubTask)) {
            List<Integer> subtasksList = ((EpicTask) newTask).getEpictaskSubtasks();
            int subtaskId = ((SubTask) oldTask).getId();
            if (subtasksList.contains(subtaskId)) {
                marker = true;
            }
        }
        return marker;
    }

    public boolean checkIntersectionTasks(Task newTask, Task oldTask) {
        boolean marker = true;
        LocalDateTime oldTaskStartTime = oldTask.getStartTime();
        LocalDateTime oldTaskEndTime = oldTask.getEndTime();
        LocalDateTime newTaskStartTime = newTask.getStartTime();
        LocalDateTime newTaskEndTime = newTask.getEndTime();

        boolean oldStartAfterNewEnd = oldTaskStartTime.isAfter(newTaskEndTime);
        boolean newStartAfterOldEnd = newTaskStartTime.isAfter(oldTaskEndTime);
        if (oldStartAfterNewEnd || newStartAfterOldEnd) {
            marker = false;
        }
        return marker;
    }

}