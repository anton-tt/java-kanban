package test;

import basic.Status;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    public Task generateTaskOne() {
        Task taskOne = new Task("Задача 1",
                "Описание Задачи 1",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 1, 1, 12, 0),
                generateDurationTask(360));
        taskManager.putNewTaskInMap(taskOne);
        return taskOne;
    }

    public Task generateTaskTwo() {
        Task taskOne = new Task("Задача 2",
                "Описание Задачи 2",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 7, 1, 12, 0),
                generateDurationTask(360));
        taskManager.putNewTaskInMap(taskOne);
        return taskOne;
    }

    public EpicTask generateEpicOne() {
        EpicTask  epicOne = new EpicTask("Эпик 1", "Описание Эпика 1", taskManager.getNextId());
        taskManager.putNewEpictaskInMap(epicOne);
        return epicOne;
    }

    public EpicTask generateEpicTwo() {
        EpicTask  epicTwo = new EpicTask("Эпик 2", "Описание Эпика 2", taskManager.getNextId());
        taskManager.putNewEpictaskInMap(epicTwo);
        return epicTwo;
    }

    public SubTask generateSubtaskOneEpicOne() {
        EpicTask epicOne = generateEpicOne();
        SubTask  subOne = new SubTask("Подзадача 1",
                "Описание Подзадачи 1 эпика 1",
                taskManager.getNextId(),
                Status.NEW,
                generateStartTimeTask(2023, 5, 10, 6, 0),
                generateDurationTask(480),
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subOne);
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
    public void putTaskInMap() {
        Task taskOne = generateTaskOne();
        int taskOneId = taskOne.getId();
        Task discoveredTask = taskManager.discoverTask(taskOneId);
        assertNotNull(discoveredTask, "Созданная задача отсутствует в списках задач.");

        Task taskTwo = generateTaskTwo();
        assertEquals(taskOne, discoveredTask, "Созданная задача и найденная в списках разные.");
    }

    @Test
    public void putEpictaskInMap() {
        EpicTask epicOne = generateEpicOne();
        int epicOneId = epicOne.getId();
        Status epicOneStatus = epicOne.getStatus();
        EpicTask discoveredEpic = (EpicTask) taskManager.discoverTask(epicOneId);
        assertNotNull(discoveredEpic, "Созданный эпик отсутствует в списках задач.");
        assertEquals(Status.NEW, epicOneStatus, "Статус эпика сформирован неправильно.");

        EpicTask epicTwo = generateEpicTwo();
        assertEquals(epicOne, discoveredEpic, "Созданный эпик и найденная в списках задача разные.");
    }

    @Test
    public void putSubtaskInMap() {
        SubTask subtaskOne = generateSubtaskOneEpicOne();
        int subtaskOneId = subtaskOne.getId();
        SubTask discoveredSubtask = (SubTask) taskManager.discoverTask(subtaskOneId);
        assertNotNull(discoveredSubtask, "Созданная подзадача отсутствует в списках задач.");
        assertEquals(subtaskOne, discoveredSubtask, "Созданная задача и найденная в списках разные.");

        int epicId = subtaskOne.getSubtaskEpictaskId();
        EpicTask discoveredEpic = (EpicTask) taskManager.discoverTask(epicId);
        assertNotNull(discoveredEpic, "Подзадача не связана ни с одним из эпиков.");
    }

    @Test
    public void getListTasks() {
        Task taskOne = generateTaskOne();
        Task taskTwo = generateTaskTwo();
        SubTask subtaskOne = generateSubtaskOneEpicOne();
        List<Task> allTask = taskManager.getListTasks();
        boolean empty = allTask.isEmpty();
        Task discoveredEpic = allTask.get(0);
        int allTaskSize = allTask.size();
        assertFalse(empty, "После создания четырёх задач обший список оказался пустой.");
        assertEquals(4, allTaskSize, "Число созданных задач и выводимых в списке не совпадают.");
        assertEquals(taskOne, discoveredEpic, "Первая созданная задача и первая задача из общего списка не совпадают.");
    }

    @Test
    public void getListTasksNull() {
        List<Task> allTask = taskManager.getListTasks();
        boolean empty = allTask.isEmpty();
        assertTrue(empty, "Ложный результат, список задач должен быть пуст.");
    }

    @Test
    public void clearMapTasks() {
        Task taskOne = generateTaskOne();
        Task taskTwo = generateTaskTwo();
        SubTask subtaskOne = generateSubtaskOneEpicOne();
        List<Task> allTask = taskManager.getListTasks();
        int allTaskSize = allTask.size();
        taskManager.clearMapTasks();
        List<Task> newAllTask = taskManager.getListTasks();
        int newAllTaskSize = newAllTask.size();
        boolean empty = newAllTask.isEmpty();
        assertNotEquals(allTaskSize, newAllTaskSize, "Размер списка задач после удаления задач не изменился.");
        assertTrue(empty, "Ложный результат, список задач должен быть пуст.");
    }

    @Test
    public void removeTask() {
        Task taskOne = generateTaskOne();
        Task taskTwo = generateTaskTwo();
        int taskOneId = taskOne.getId();
        taskManager.removeTask(taskOneId);
        Task newTask = taskManager.discoverTask(taskOneId);
        assertNull(newTask, "Задача не была удалена.");
    }

    @Test
    public void removeFalseTask(){
        Task taskOne = generateTaskOne();
        List<Task> allTask = taskManager.getListTasks();
        int allTaskSize = allTask.size();
        taskManager.removeTask(10);
        assertEquals(1, allTaskSize, "При удалении отсутствующей задачи удаляется существующая.");
    }

    @Test
    public void getRequiredTask() {
        Task taskOne = generateTaskOne();
        int taskOneId = taskOne.getId();
        Task taskTwo = generateTaskTwo();
        Task discoveredOneTask = taskManager.discoverTask(taskOneId);
        assertEquals(taskOne, discoveredOneTask, "Созданная и найденная по id задачи не совпадают.");

        SubTask subtaskOne = generateSubtaskOneEpicOne();
        int subtaskOneId = subtaskOne.getId();
        SubTask discoveredOneSubTask = (SubTask) taskManager.discoverTask(subtaskOneId);
        assertEquals(subtaskOne, discoveredOneSubTask, "Созданная и найденная по id подзадачи не совпадают.");

        int epicOneId = subtaskOne.getSubtaskEpictaskId();
        EpicTask epicOne = (EpicTask) taskManager.discoverTask(epicOneId);
        assertNotNull(epicOne, "Эпик подзадачи не найден по id");
    }

    @Test
    public void getRequiredTaskNull() {
        Task task = taskManager.discoverTask(100);
        assertNull(task, "Ложный результат, задачи не создавались, списки пусты.");
    }

    @Test
    public void getRequiredFalseTask() {
        Task taskOne = generateTaskOne();
        Task taskTwo = generateTaskTwo();
        Task falseTask = taskManager.discoverTask(100);
        assertNull(falseTask, "Ложный результат, список непуст, но такой задачи не создавалось.");
    }

    @Test
    public void getPrioritizedTasks() {
        List<Task> taskList = new ArrayList<>();
        Task taskTwo = generateTaskTwo();
        EpicTask epicOne = generateEpicOne();
        Task taskPriority = generateTaskOne();
        taskList.add(taskPriority);
        taskList.add(taskTwo);
        taskList.add(epicOne);
        List<Task> priorityList = taskManager.getPrioritizedTasks();
        assertEquals(taskList, priorityList, "Эталонный список и отсортированный по приоритету не совпали.");
    }

    @Test
    public void getPrioritizedTasksNull() {
        List<Task> priorityList = taskManager.getPrioritizedTasks();
        boolean empty = priorityList.isEmpty();
        assertTrue(empty, "Ложный результат, задачи отсутствуют, но список приоритетов не пуст");
    }

   @Test
    public void updateTask() {
        Task oldTask = generateTaskOne();
        int oldtaskId = oldTask.getId();
        Task newTask = new Task("Задача 1",
                "Обновлённая",
                taskManager.getNextId(),
                Status.DONE,
                generateStartTimeTask(2023, 1, 1, 12, 0),
                generateDurationTask(360)
        );
        taskManager.updateTask(oldTask, newTask);
        Task discoveredOldTask = taskManager.discoverTask(oldtaskId);
        Task discoveredNewTask = taskManager.discoverTask(newTask.getId());
        assertNull(discoveredOldTask, "Начальный вариант задачи сохраняется в списке после обновления");
        assertNotNull(discoveredNewTask, "Новый вариант задачи отсутствует в списке созданных задач.");
        assertEquals(newTask, discoveredNewTask, "Созданная задача (новый вариант) и найденная в списке не идентичны.");
    }

    @Test
    public void updateSubtaskAndEpic() {
        SubTask oldSubtask = generateSubtaskOneEpicOne();
        int oldSubtaskId = oldSubtask.getId();
        int epicId = oldSubtask.getSubtaskEpictaskId();

        SubTask  newSubtask = new SubTask("Подзадача 1",
                "Обновлённая",
                taskManager.getNextId(),
                Status.DONE,
                generateStartTimeTask(2023, 5, 10, 6, 0),
                generateDurationTask(480),
                epicId);

        EpicTask epic = (EpicTask) taskManager.discoverTask(epicId);
        Status epicOldStatus = epic.getStatus();

        taskManager.updateSubtask(oldSubtask, newSubtask);
        Status epicNewStatus = epic.getStatus();

        SubTask discoveredOldSubtask = (SubTask) taskManager.getRequiredTask(oldSubtaskId);
        SubTask discoveredNewSubtask = (SubTask) taskManager.getRequiredTask(newSubtask.getId());
        EpicTask discoveredEpic = (EpicTask) taskManager.getRequiredTask(newSubtask.getSubtaskEpictaskId());

        assertEquals(Status.NEW, epicOldStatus, "Статус эпика после создания подзадачи сформирован неправильно.");
        assertNull(discoveredOldSubtask, "Начальный вариант подзадачи не удаляется из списка после обновления");
        assertNotNull(discoveredNewSubtask, "Новый вариант подзадачи не добавился в список созданных задач.");
        assertNotNull(discoveredEpic, "После обновления подзадачи эпик не сохраняется в списке задач.");
        assertEquals(Status.DONE, epicNewStatus, "Статус обновлённого эпика сформирован неправильно.");
    }

}