package basic;
import tasks.*;
import managers.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Создаём две простые задачи.");
        Task taskOne = new Task("Праздничн. обед", "Из 9 блюд на 10 персон", taskManager.getNextId(), Status.NEW);
        taskManager.putNewTaskInMap(taskOne);
        Task taskTwo = new Task("Генеральн.уборка", "На след. день после обеда", taskManager.getNextId(), Status.NEW);
        taskManager.putNewTaskInMap(taskTwo);
        System.out.println("Задачи:");
        printTask(taskOne);
        printTask(taskTwo);

        System.out.println("");
        System.out.println("Создаём два эпика без подзадач.");
        EpicTask epicOne = new EpicTask("Путешествие", "Из Петербурга в Москву", taskManager.getNextId());
        taskManager.putNewEpictaskInMap(epicOne);
        EpicTask epicTwo = new EpicTask("Вернуться домой", "Транзитом через Минск", taskManager.getNextId()/*, manager.subtasksOneEpicList*/);
        taskManager.putNewEpictaskInMap(epicTwo);
        System.out.println("Эпики:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);

        System.out.println("");
        System.out.println("Создаём подзадачи для эпиков.");
        SubTask subOne = new SubTask("Петербург-Москва", "Поезд", taskManager.getNextId(), Status.NEW, epicOne.getId());
        taskManager.putNewSubtaskInMap(subOne);
        SubTask subTwo = new SubTask("Москва-Минск", "Автобус", taskManager.getNextId(), Status.NEW, epicTwo.getId());
        taskManager.putNewSubtaskInMap(subTwo);
        SubTask subThree = new SubTask("Минск-Петербург", "Самолёт", taskManager.getNextId(), Status.NEW, epicTwo.getId());
        taskManager.putNewSubtaskInMap(subThree);
        System.out.println("Эпики и их подзадачи:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);
        printSubTask(subOne);
        printSubTask(subTwo);
        printSubTask(subThree);

        System.out.println("");
        System.out.println("Обновляем первую задачу, единственную подзадачу первого эпика, первую подзадачу второго эпика");
        Task taskNull = new Task("Праздничн. обед", "Из 9 блюд на 10 персон", taskManager.getNextId(), Status.DONE);
        taskManager.updateTask(taskOne, taskNull);
        SubTask subFour = new SubTask("Петербург-Москва", "Поезд", taskManager.getNextId(), Status.DONE, epicOne.getId());
        taskManager.updateSubtask(subOne, subFour);
        SubTask subFive = new SubTask("Москва-Минск", "Автобус", taskManager.getNextId(), Status.IN_PROGRESS, epicTwo.getId());
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
        System.out.println("Удаляем первый эпик (id 2), проверяем - ищем его и подзадачу (id 4)");
        taskManager.removeTask(2);
        taskManager.getRequiredTask(4);

        System.out.println("");
        System.out.println("Просматриваем 10 произвольных задач и выводим список просмотренных задач");
        for(int i = 0; i < 11; i++) {
            taskManager.getRequiredTask(i);
        }
        taskManager.getHistory();
    }

    public static void printTask(Task task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.status);
    }
    public static void printEpicTask(EpicTask task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.getStatus());
        System.out.println("Подзадачи эпика: " + task.getEpictaskSubtasks());
    }
    public static void printSubTask(SubTask task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.status);
        System.out.println("Эпик подзадачи: " + task.getSubtaskEpictaskId());
    }

}