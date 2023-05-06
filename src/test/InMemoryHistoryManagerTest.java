package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import basic.Status;
import managers.*;
import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HistoryManager historyManager= new InMemoryHistoryManager();

    public Task generateTaskOne() {
        Task taskOne = new Task("Задача 1",
                "Описание Задачи 1",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 1, 1, 12, 0),
                generateDurationTask(360));
        return taskOne;
    }

    public Task generateTaskTwo() {
        Task taskOne = new Task("Задача 2",
                "Описание Задачи 2",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 7, 1, 12, 0),
                generateDurationTask(360));
        return taskOne;
    }

    public EpicTask generateEpicOne() {
        EpicTask  epicOne = new EpicTask("Эпик 1", "Описание Эпика 1", taskManager.getNextId());
        return epicOne;
    }

    public EpicTask generateEpicTwo() {
        EpicTask  epicTwo = new EpicTask("Эпик 2", "Описание Эпика 2", taskManager.getNextId());
        return epicTwo;
    }

    public SubTask generateSubtask(int epicId) {
        SubTask  subOne = new SubTask("Подзадача 1",
                "Описание Подзадачи 1 эпика 1",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 5, 10, 6, 0),
                generateDurationTask(480),
                epicId);
        return subOne;
    }

    public LocalDateTime generateStartTimeTask(int year, int month, int day, int hour, int minute) {
        LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, minute);
        return startTime;
    }

    public Duration generateDurationTask(long minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        return duration;
    }
    @Test
    public void getHistoryNull() {
        List<Task> historyList = taskManager.getHistory();
        boolean empty = historyList.isEmpty();
        assertTrue(empty, "Ложный результат, задачи не просматривались, но список не пуст");
    }

    @Test
    public void addViewedTaskGetHistory() {
        List<Task> newHistoryList = new ArrayList<>();
        Task taskOne = generateTaskOne();
        historyManager.addViewedTask(taskOne);
        newHistoryList.add(taskOne);
        Task taskTwo = generateTaskTwo();
        historyManager.addViewedTask(taskTwo);
        newHistoryList.add(taskTwo);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(newHistoryList, historyList, "Списки просмотренных задач и истории просмотров не совпали.");
    }

    @Test
    public void addViewedTaskRecurringTaskGetHistory() {
        List<Task> newHistoryList = new ArrayList<>();
        Task taskOne = generateTaskOne();
        historyManager.addViewedTask(taskOne);
        newHistoryList.add(taskOne);
        Task taskTwo = generateTaskTwo();
        historyManager.addViewedTask(taskTwo);
        newHistoryList.add(taskTwo);
        historyManager.addViewedTask(taskOne);
        historyManager.addViewedTask(taskTwo);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(newHistoryList, historyList, "История просмотров не совпала с эталонным списком, вариант 1.");

        newHistoryList.clear();
        newHistoryList.add(taskTwo);
        newHistoryList.add(taskOne);
        historyManager.addViewedTask(taskOne);
        historyList = historyManager.getHistory();
        assertEquals(newHistoryList, historyList, "История просмотров не совпала с эталонным списком, вариант 2.");
    }

    @Test
    public void addViewedTaskEpicSubtaskGetHistory() {
        List<Task> newHistoryList = new ArrayList<>();
        Task taskOne = generateTaskOne();
        historyManager.addViewedTask(taskOne);
        newHistoryList.add(taskOne);
        EpicTask epicOne = generateEpicOne();
        historyManager.addViewedTask(epicOne);
        newHistoryList.add(epicOne);
        SubTask subtaskOne = generateSubtask(epicOne.getId());
        historyManager.addViewedTask(subtaskOne);
        newHistoryList.add(subtaskOne);
        EpicTask epicTwo = generateEpicTwo();
        historyManager.addViewedTask(epicTwo);
        newHistoryList.add(epicTwo);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(newHistoryList, historyList, "История просмотров не совпала с эталонным списком.");
    }

}