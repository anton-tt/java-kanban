package test;

import managers.FileBackedTasksManager;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.List;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file = new File("resource\\fileTest.csv");

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(file.getPath());
    }

    @Test
    public void putOneTask()  {
        generateTaskOne();
        List<Task> historyList = taskManager.getListTasks();
        taskManager.save();
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> newHistoryList = newTaskManager.getListTasks();
        assertEquals(newHistoryList, historyList, "История просмотров до и после восстановления состояния не совпадают.");
    }

    @Test
    public void putNull()  {
        List<Task> historyList = taskManager.getListTasks();
        taskManager.save();
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> newHistoryList = newTaskManager.getListTasks();
        assertEquals(newHistoryList, historyList, "Ложный результат: задачи не создавались, не просматривались, но истории отличаются.");
    }

    @Test
    public void putAndSeeTaskEpic()  {
        Task taskOne = generateTaskOne();
        EpicTask epicOne = generateEpicOne();
        taskManager.getRequiredTask(taskOne.getId());
        taskManager.getRequiredTask(epicOne.getId());
        List<Task> historyList = taskManager.getListTasks();
        taskManager.save();
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> newHistoryList = newTaskManager.getListTasks();
        assertEquals(newHistoryList, historyList, "Истории просмотров не совпали.");
    }

    @Test
    public void putTaskEpicSubtask()  {
        Task taskOne = generateTaskOne();
        SubTask subtaskOne = generateSubtaskOneEpicOne();
        taskManager.getRequiredTask(taskOne.getId());
        taskManager.getRequiredTask(subtaskOne.getId());
        List<Task> historyList = taskManager.getListTasks();
        taskManager.save();
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> newHistoryList = newTaskManager.getListTasks();
        assertEquals(newHistoryList, historyList, "Истории просмотров не совпали.");
    }

}