package managers;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import exception.ManagerSaveException;
import basic.Type;
import tasks.*;
import basic.Status;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path repository;

    public FileBackedTasksManager(String file) {
        repository = Paths.get(file);
    }
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void putNewTaskInMap(Task task) {
        super.putNewTaskInMap(task);
        save();
    }

    @Override
    public void putNewEpictaskInMap(EpicTask epictask) {
        super.putNewEpictaskInMap(epictask);
        save();
    }

    @Override
    public void putNewSubtaskInMap(SubTask subtask) {
        super.putNewSubtaskInMap(subtask);
        save();
    }

    @Override
    public List<Task> getListTasks(){
        super.getListTasks();
        save();
        return allTasksList;
    }

    @Override
    public void clearMapTasks(){
        super.clearMapTasks();
        save();
    }

    @Override
    public void removeTask(int id){
        super.removeTask(id);
        save();
    }

    @Override
    public Task getRequiredTask(int id){
        Task requiredTask = super.getRequiredTask(id);
        save();
        return requiredTask;
    }
    @Override
    public List<Task> getHistory(){
        List<Task> listViewedTask = historyManager.getHistory();
        save();
        return listViewedTask;
    }

    @Override
    public void updateTask(Task oldTask, Task newTask){
        super.updateTask(oldTask, newTask);
        save();
    }

    @Override
    public void updateSubtask(SubTask oldTask, SubTask newTask){
        super.updateSubtask(oldTask, newTask);
        save();
    }

    @Override
    public void updateEpictask(EpicTask epictask){
        super.updateEpictask(epictask);
        save();
    }

    public String toString(Task task) {
        String taskString = null;
        Type typeTask = null;
        LocalDateTime startTimeTask = task.getStartTime();
        Duration durationTask = task.getDuration();
        int subtaskEpictaskId = 0;
        if (task instanceof EpicTask) {
            typeTask = Type.EPICTASK;
        }
        else if (task instanceof SubTask) {
            typeTask = Type.SUBTASK;
            subtaskEpictaskId = ((SubTask) task).getSubtaskEpictaskId();
        }
        else {
            typeTask = Type.TASK;
        }

        String initialTaskString = task.getId() + "," +
                typeTask + "," +
                task.name + "," +
                task.getStatus() + "," +
                task.description;
        if(startTimeTask != null) {
            initialTaskString = initialTaskString + "," +
                    startTimeTask + "," +
                    durationTask.toMinutes();
        }

        if (subtaskEpictaskId != 0) {
            taskString = initialTaskString + "," + subtaskEpictaskId;
        } else {
            taskString = initialTaskString;
        }
        return taskString;
    }

    public static Task fromString(String value) {
        String[] taskString = value.split(",");
        int id = Integer.parseInt(taskString[0]);
        Type typeTask = Type.valueOf(taskString[1]);
        String name = taskString[2];
        Status status = Status.valueOf(taskString[3]);
        String description = taskString[4];
        LocalDateTime startTime = null;
        Duration duration = null;
        if (taskString.length > 5) {
            startTime = LocalDateTime.parse(taskString[5]);
            long minuteDuration = Long.parseLong(taskString[6]);
            duration = Duration.ofMinutes(minuteDuration);
        }        Task task = null;
        if(typeTask.equals(Type.TASK)) {
            task = new Task(name, description, id, status, startTime, duration);

        } else if (typeTask.equals(Type.SUBTASK)) {
            int subtaskEpictaskId = Integer.parseInt(taskString[7]);
            task = new SubTask(name, description, id, status, startTime, duration, subtaskEpictaskId);
        } else if (typeTask.equals(Type.EPICTASK)) {
            task = new EpicTask(name, description, id);
        }
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder historyStringBuilder = new StringBuilder();
        List<Task> listTask = manager.getHistory();
        if (listTask.size() > 1) {
            for (Task task : listTask) {
                historyStringBuilder.append(task.getId());
                historyStringBuilder.append(",");
            }
        } else {
            for (Task task : listTask) {
                historyStringBuilder.append(task.getId());
            }
        }
        String historyString = historyStringBuilder.toString();
        return historyString;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> listTaskId = new ArrayList<>();
        if (!value.isBlank()) {
            String[] tasksId = value.split(",");
            for(int i = 0; i < tasksId.length; i++) {
                Integer id = Integer.parseInt(/*taskId*/tasksId[i]);
                listTaskId.add(id);
            }
        }
        return listTaskId;
    }

    public void save()  {
        String fileName =  repository.toString();
        String transfer = System.lineSeparator();

        try (Writer fileWriter = new FileWriter(fileName);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {
            bufferWriter.write("id,type,name,status,description,startTime,duration,epic");
            bufferWriter.write(transfer);
            for(Task task : taskMap.values()) {
                bufferWriter.write(toString(task));
                bufferWriter.write(transfer);
            }
            for(Task task : epicMap.values()) {
                bufferWriter.write(toString(task));
                bufferWriter.write(transfer);
            }
            for(Task task : subtaskMap.values()) {
                bufferWriter.write(toString(task));
                bufferWriter.write(transfer);
            }
            bufferWriter.write(transfer);
            bufferWriter.write(historyToString(historyManager)); // !!!!!

        } catch (IOException exception) {
            throw new ManagerSaveException("При работе с файлом возникла ошибка!");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager currentInstance = new FileBackedTasksManager(file.getPath());

        try (Reader fileReader = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(fileReader)) {
            bufferReader.readLine();
            while (bufferReader.ready()) {
                String element = bufferReader.readLine();
                if (element.isBlank()) {
                    break;
                }
                Task task = fromString(element);
                if (task instanceof EpicTask) {
                    currentInstance.putNewEpictaskInMap((EpicTask) task);
                } else if (task instanceof SubTask) {
                    currentInstance.putNewSubtaskInMap((SubTask) task);
                } else if (task != null) {
                    currentInstance.putNewTaskInMap(task);
                }
            }
            String elHistory = bufferReader.readLine();
            if(elHistory != null) {
                List<Integer> recreatedList = historyFromString(elHistory);
                    for (Integer idTask : recreatedList) {
                       currentInstance.getRequiredTask(idTask);
                    }
                }
        } catch (IOException exception) {
            System.out.println("При работе с файлом возникла ошибка!");
        }
        return currentInstance;
    }

}