package basic;

import server.HttpTaskServer;
import server.KVServer;
import tasks.*;
import managers.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();
        HttpTaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();

        System.out.println("Создаём две простые задачи.");
        Task taskOne = new Task("Праздничн. обед",
                "Из 9 блюд на 10 персон",
                Status.NEW,
                "01.05.2023 12:00",
                240);
        taskManager.putNewTaskInMap(taskOne);
        taskManager.getRequiredTask(1);

        Task taskTwo = new Task("Генеральн.уборка",
                "На след. день после обеда",
                Status.NEW,
                "02.05.2023 12:00",
                240);
        taskManager.putNewTaskInMap(taskTwo);
        System.out.println("Задачи:");
        printTask(taskOne);
        printTask(taskTwo);

        System.out.println("");
        System.out.println("Создаём два эпика без подзадач.");
        EpicTask epicOne = new EpicTask("Путешествие", "Из Петербурга в Москву");
        taskManager.putNewEpictaskInMap(epicOne);
        EpicTask epicTwo = new EpicTask("Вернуться домой", "Транзитом через Минск");
        taskManager.putNewEpictaskInMap(epicTwo);
        System.out.println("Эпики:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);

        System.out.println("");
        System.out.println("Создаём подзадачи для эпиков.");
        SubTask subOne = new SubTask("Петербург-Москва",
                "Поезд",
                Status.NEW,
                "08.05.2023 06:00",
                360,
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subOne);
        SubTask subTwo = new SubTask("Москва-Минск",
                "Автобус",
                Status.NEW,
                "10.05.2023 06:00",
                480,
                epicTwo.getId());
        taskManager.putNewSubtaskInMap(subTwo);
        SubTask subThree = new SubTask("Минск-Петербург",
                "Самолёт",
                Status.NEW,
                "12.05.2023 06:00",
                600,
                epicTwo.getId());
        taskManager.putNewSubtaskInMap(subThree);
        System.out.println("Эпики и их подзадачи:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);
        printSubTask(subOne);
        printSubTask(subTwo);
        printSubTask(subThree);

        System.out.println("");
        System.out.println("Обновляем первую задачу, единственную подзадачу первого эпика, первую подзадачу второго эпика");
        Task taskNull = new Task("Праздничн. обед",
                "Из 9 блюд на 10 персон",
                Status.DONE,
                "01.05.2023 12:00",
                240);
        taskManager.updateTask(taskOne, taskNull);
        SubTask subFour = new SubTask("Петербург-Москва",
                "Поезд",
                Status.DONE,
                "08.05.2023 06:00",
                360,
                epicOne.getId());
        taskManager.updateSubtask(subOne, subFour);
        SubTask subFive = new SubTask("Москва-Минск",
                "Автобус",
                Status.IN_PROGRESS,
                "10.05.2023 06:00",
                480,
                epicTwo.getId());
        taskManager.updateSubtask(subTwo, subFive);
        System.out.println("После обновления:");
        System.out.println("Задачи:");
        printTask(taskNull);
        printTask(taskTwo);
        System.out.println("Эпики:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);
        System.out.println("Подзадачи:");
        printSubTask(subThree);
        printSubTask(subFour);
        printSubTask(subFive);

        System.out.println("");
        System.out.println("Удаляем первый эпик (id 3), проверяем - ищем его и подзадачу (id 9)");
        taskManager.removeTask(3);
        taskManager.getRequiredTask(3);
        taskManager.getRequiredTask(9);
        taskManager.getRequiredTask(8);
        taskManager.getRequiredTask(6);

        System.out.println("");
        System.out.println("Просматриваем 10 произвольных задач (+ дополнительно) и выводим список просмотренных задач");
        for(int i = 0; i < 11; i++) {
            taskManager.getRequiredTask(i);
        }
        taskManager.getRequiredTask(1);
        taskManager.getRequiredTask(3);
        taskManager.getRequiredTask(7);
        taskManager.getRequiredTask(8);
        taskManager.getRequiredTask(1);
        taskManager.getRequiredTask(3);
        taskManager.getRequiredTask(13);
        taskManager.getRequiredTask(7);
        taskManager.getHistory();

        System.out.println("");
        System.out.println("Создаём третий эпик - конкурент");
        EpicTask epicThree = new EpicTask("Выгорание", "Отменить праздничный обед");
        taskManager.putNewEpictaskInMap(epicThree);
        printEpicTask(epicThree);
        System.out.println("Создаём подзадачу третьего эпика");
        SubTask subSix = new SubTask("Лень",
                "Никого не жду",
                Status.NEW,
                "01.05.2023 12:00",
                720,
                epicOne.getId());
        taskManager.putNewSubtaskInMap(subSix);
        System.out.println("Эпик-конкурент(id 11) и его подзадача (id 12):");
        printEpicTask(epicThree);
        taskManager.getRequiredTask(11);
        taskManager.getRequiredTask(12);
        printSubTask(subSix);

        System.out.println("");
        System.out.println("Создаём третью задачу с самым ранним сроком исполнения");
        Task taskThree = new Task("Продукты",
                "Закупить для праздничн. обеда",
                Status.NEW,
                "30.04.2023 19:00",
                90
        );
        taskManager.putNewTaskInMap(taskThree);
        printTask(taskThree);
        taskManager.getPrioritizedTasks();

        System.out.println("");
        taskServer.stop();
        server.stop();
    }

    public static void printTask(Task task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.status);
        System.out.println("Дата начала: " + task.getStartTime());
        System.out.println("Длительность: " + task.getDuration());
        System.out.println("Дата окончания: " + task.getEndTime());
    }
    public static void printEpicTask(EpicTask task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.getStatus());
        System.out.println("Дата начала: " + task.getStartTime());
        System.out.println("Длительность: " + task.getDuration());
        System.out.println("Дата окончания: " + task.getEndTime());
        System.out.println("Подзадачи эпика: " + task.getEpictaskSubtasks());

    }
    public static void printSubTask(SubTask task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.status);
        System.out.println("Дата начала: " + task.getStartTime());
        System.out.println("Длительность: " + task.getDuration());
        System.out.println("Дата окончания: " + task.getEndTime());
        System.out.println("Эпик подзадачи: " + task.getSubtaskEpictaskId());

    }
    
}