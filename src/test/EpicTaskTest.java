package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import managers.*;
import tasks.*;
import basic.Status;
import java.time.Duration;
import java.time.LocalDateTime;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epicOne;

    private void generateSubtasks(Status statusOne, Status statusTwo) {
        SubTask subOne = new SubTask("Москва-Минск",
                "Автобус",
                taskManager.getNextId(),
                statusOne,
                taskManager.generateStartTimeTask(2023, 5, 10, 6, 00),
                taskManager.generateDurationTask(480),
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subOne);

        SubTask subTwo = new SubTask("Минск-Петербург",
                "Самолёт",
                taskManager.getNextId(),
                statusTwo,
                taskManager.generateStartTimeTask(2023, 5, 12, 6, 00),
                taskManager.generateDurationTask(600),
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subTwo);
    }

    private void generateSubtask(int year, int month, int day, int hour, int minute, long minutes) {
        SubTask subThree = new SubTask("Москва-Минск",
                "Автобус",
                taskManager.getNextId(),
                Status.NEW,
                taskManager.generateStartTimeTask(year, month, day, hour, minute),
                taskManager.generateDurationTask(minutes),
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subThree);
    }

    @BeforeEach
        public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        epicOne = new EpicTask("Вернуться домой", "Транзитом через Минск", taskManager.getNextId());
        taskManager.putNewEpictaskInMap(epicOne);
    }

    @Test
    public void generateStatusEpicSubtaskNull() {
        taskManager.setStatusEpic(epicOne);
        Status epicOneStatus = epicOne.getStatus();

        assertEquals(Status.NEW, epicOneStatus, "Статус эпика без подзадачи формируется неправильно.");
    }

    @Test
    public void generateStatusEpicSubtaskStatusNew() {
        generateSubtasks(Status.NEW, Status.NEW);
        Status epicOneStatus = epicOne.getStatus();

        assertEquals(Status.NEW, epicOneStatus, "Статус эпика с двумя NEW-подзадачами формируется неправильно.");
    }

    @Test
    public void generateStatusEpicSubtaskStatusInProgress() {
        generateSubtasks(Status.IN_PROGRESS, Status.IN_PROGRESS);
        Status epicOneStatus = epicOne.getStatus();

        assertEquals(Status.IN_PROGRESS, epicOneStatus, "Статус эпика с двумя IN_PROGRESS-подзадачами формируется неправильно.");
    }

    @Test
    public void generateStatusEpicSubtaskStatusDone() {
        generateSubtasks(Status.DONE, Status.DONE);
        Status epicOneStatus = epicOne.getStatus();

        assertEquals(Status.DONE, epicOneStatus, "Статус эпика с двумя DONE-подзадачами формируется неправильно.");
    }

    @Test
    public void generateStatusEpicSubtaskStatusDoneStatusNew() {
        generateSubtasks(Status.DONE, Status.NEW);
        Status epicOneStatus = epicOne.getStatus();

        assertEquals(Status.IN_PROGRESS, epicOneStatus, "Статус эпика с двумя разными подзадачами формируется неправильно.");
    }

    @Test
    public void setStartTimeAndDurationEpicSubtaskNull() {
        taskManager.setStartTimeEpic(epicOne);
        LocalDateTime epicStartTime = epicOne.getStartTime();
        taskManager.setDurationEpic(epicOne);
        Duration epicDuration =  epicOne.getDuration();
        LocalDateTime epicEndTime = epicOne.getEndTime();

        assertNull(epicStartTime, "У эпика без подзадач не должно быть даты начала.");
        assertNull(epicDuration, "У эпика без подзадач не должно быть продолжительности.");
        assertNull(epicEndTime, "У эпика без подзадач не должно быть даты окончания.");
    }

    @Test
    public void setStartTimeAndDurationEpicSubtaskOne() {
        generateSubtask(2023, 6, 1, 12, 00, 360);
        LocalDateTime standardStartTime = LocalDateTime.of(2023, 6, 1, 12, 00);
        LocalDateTime epicStartTime = epicOne.getStartTime();
        Duration standardDuration = Duration.ofMinutes(360);
        Duration epicDuration =  epicOne.getDuration();
        LocalDateTime standardEndTime = standardStartTime.plus(standardDuration);
        LocalDateTime epicEndTime = epicOne.getEndTime();

        assertEquals(standardStartTime, epicStartTime, "У эпика с одной подзадачей неправильно сформирована дата начала.");
        assertEquals(standardDuration, epicDuration, "У эпика с одной подзадачей неправильно сформирована продолжительность.");
        assertEquals(standardEndTime, epicEndTime, "У эпика с одной подзадачей  неправильно сформирована дата окончания.");
    }

    @Test
    public void setStartTimeAndDurationEpicSubtaskTwo() {
        generateSubtask(2023, 6, 1, 12, 00, 360);
        generateSubtask(2023, 6, 1, 18, 01, 180);
        LocalDateTime standardStartTime = LocalDateTime.of(2023, 6, 1, 12, 00);
        LocalDateTime epicStartTime = epicOne.getStartTime();
        Duration standardDuration = Duration.ofMinutes(540);
        Duration epicDuration =  epicOne.getDuration();
        LocalDateTime standardEndTime = standardStartTime.plus(standardDuration).plus(Duration.ofSeconds(60)); // прибавили 60 секунд "перерыва" между окончанием старой и началом новой подзадачи
        LocalDateTime epicEndTime = epicOne.getEndTime();

        assertEquals(standardStartTime, epicStartTime, "У эпика с двумя подзадами неправильно сформирована дата начала.");
        assertEquals(standardDuration, epicDuration, "У эпика с двумя подзадами неправильно сформирована продолжительность.");
        assertEquals(standardEndTime, epicEndTime, "У эпика с двумя подзадами неправильно сформирована дата окончания.");
    }

    @Test
    public void setStartTimeAndDurationEpicSubtaskRemove() {
        generateSubtask(2023, 6, 1, 12, 00, 360);
        LocalDateTime epicStartTime = epicOne.getStartTime();
        Duration epicDuration =  epicOne.getDuration();
        int subtaskId = (epicOne.getEpictaskSubtasks()).get(0);
        taskManager.removeTask(subtaskId);
        LocalDateTime epicNewStartTime = epicOne.getStartTime();
        Duration epicNewDuration =  epicOne.getDuration();

        assertNotEquals(epicStartTime, epicNewStartTime, "После удаления подзадачи у эпика дата начала не изменилась");
        assertNull(epicNewStartTime, "После удаления единственной подзадачи у эпика дата начала не обнулилась");
        assertNotEquals(epicDuration, epicNewDuration, "После удаления подзадачи у эпика продолжительность не изменилась");
        assertNull(epicNewDuration, "После удаления единственной подзадачи у эпика продолжительность не обнулилась");
    }

}