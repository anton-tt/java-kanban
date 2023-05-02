package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import server.KVTaskClient;
import tasks.*;
import typeAdapter.*;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String TASKS = "task";
    private static final String EPICTASKS = "epic";
    private static final String SUBTASKS = "subtask";
    private static final String HISTORY = "history";

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final KVTaskClient taskClient;

    public HttpTaskManager(String KVServerURL) throws IOException, InterruptedException {
        super();
        taskClient = new KVTaskClient(KVServerURL);
    }

    @Override
    public void save() {
        taskClient.put(TASKS, gson.toJson(taskMap));
        taskClient.put(EPICTASKS, gson.toJson(epicMap));
        taskClient.put(SUBTASKS, gson.toJson(subtaskMap));

        List<Task> listTask = getHistory();
        List<Integer> history = new ArrayList<>();
        for (Task task : listTask) {
            history.add(task.getId());
        }
        taskClient.put(HISTORY, gson.toJson(taskMap));
    }

    public static HttpTaskManager loadFromServer(String KVServerURL) throws IOException, InterruptedException {
        HttpTaskManager currentInstance = new HttpTaskManager(KVServerURL);
        KVTaskClient currentClient = currentInstance.taskClient;
        Type typeMap = new TypeToken<HashMap<Integer, Task>>() {}.getType();
        String jsonTask = currentClient.load(TASKS);
        HashMap<Integer, Task> tasksMap = gson.fromJson(jsonTask, typeMap);
        currentInstance.taskMap.putAll(tasksMap);

        typeMap = new TypeToken<HashMap<Integer, EpicTask>>() {}.getType();
        String jsonEpictask = currentClient.load(EPICTASKS);
        HashMap<Integer, Task> epictasksMap = gson.fromJson(jsonEpictask, typeMap);
        currentInstance.taskMap.putAll(epictasksMap);

        typeMap = new TypeToken<HashMap<Integer, SubTask>>() {}.getType();
        String jsonSubtask = currentClient.load(SUBTASKS);
        HashMap<Integer, Task> subtasksMap = gson.fromJson(jsonSubtask, typeMap);
        currentInstance.taskMap.putAll(subtasksMap);

        Type typeList = new TypeToken<List<Task>>() {}.getType();
        String jsonHistory = currentClient.load(HISTORY);
        List<Task> historyList = gson.fromJson(jsonHistory, typeList);
        Map<Integer, Task> allTasksMap = new HashMap<>();
        allTasksMap.putAll(currentInstance.taskMap);
        allTasksMap.putAll(currentInstance.epicMap);
        allTasksMap.putAll(currentInstance.subtaskMap);
        for (Task task : historyList) {
            currentInstance.historyManager.addViewedTask(allTasksMap.get(task.getId()));
        }
        return currentInstance;
    }

}