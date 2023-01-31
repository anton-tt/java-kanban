public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        // создаём две задачи
        Task taskOne = new Task("Праздничн. обед", "Из 9 блюд на 10 персон", manager.getNextId(), Status.NEW);
        manager.putNewTaskInMap(taskOne);
        Task taskTwo = new Task("Генеральн.уборка", "На след. день после обеда", manager.getNextId(), Status.NEW);
        manager.putNewTaskInMap(taskTwo);
        System.out.println("Задачи:");
        printTask(taskOne);
        printTask(taskTwo);

        // создаём два эпика
        EpicTask epicOne = new EpicTask("Путешествие", "Из Петербурга в Москву", manager.getNextId(), manager.subtasksOneEpicMap);
        manager.putNewEpictaskInMap(epicOne);
        EpicTask epicTwo = new EpicTask("Вернуться домой", "Транзитом через Минск", manager.getNextId(), manager.subtasksOneEpicMap);
        manager.putNewEpictaskInMap(epicTwo);
        System.out.println("");
        System.out.println("Эпики:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);

        // создаём подзадачи для эпиков
        SubTask subOne = new SubTask("Петербург-Москва", "Поезд", manager.getNextId(), Status.NEW, epicOne);
        manager.putNewSubtaskInMap(subOne);
        SubTask subTwo = new SubTask("Москва-Минск", "Автобус", manager.getNextId(), Status.NEW, epicTwo);
        manager.putNewSubtaskInMap(subTwo);
        SubTask subThree = new SubTask("Минск-Петербург", "Самолёт", manager.getNextId(), Status.NEW, epicTwo);
        manager.putNewSubtaskInMap(subThree);
        System.out.println("");
        System.out.println("Эпики и их подзадачи:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);
        printSubTask(subOne);
        printSubTask(subTwo);
        printSubTask(subThree);

        // обновляем первую задачу, единственную подзадачу первого эпика, первую подзадачу второго эпика
        Task taskNull = new Task("Праздничн. обед", "Из 9 блюд на 10 персон", manager.getNextId(), Status.DONE);
        manager.updateTask(taskOne, taskNull);
        SubTask subFour = new SubTask("Петербург-Москва", "Поезд", manager.getNextId(), Status.DONE, epicOne);
        manager.updateSubtask(subOne, subFour);
        SubTask subFive = new SubTask("Москва-Минск", "Автобус", manager.getNextId(), Status.IN_PROGRESS, epicTwo);
        manager.updateSubtask(subTwo, subFive);
        System.out.println("");
        System.out.println("После обновления:");
        System.out.println("Задачи:");
        printTask(taskNull);
        printTask(taskTwo);
        System.out.println("");
        System.out.println("Эпики:");
        printEpicTask(epicOne);
        printEpicTask(epicTwo);
        System.out.println("");
        System.out.println("Подзадачи:");
        printSubTask(subThree);
        printSubTask(subFour);
        printSubTask(subFive);

        //удаляем первый эпик (id 2), ищем его и подзадачу (id 4)
        manager.removeTask(2);
        manager.getRequiredTask(4);
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
        System.out.println("Подзадачи эпика: " + (task.getEpictaskSubtasks()).values());
    }
    public static void printSubTask(SubTask task) {
        System.out.println("ID: " + task.getId());
        System.out.println("Название: " + task.name);
        System.out.println("Описание: " + task.description);
        System.out.println("Статус: " + task.status);
        System.out.println("Эпик подзадачи: " + task.getSubtaskEpictask());
    }

}
