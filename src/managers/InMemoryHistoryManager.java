package managers;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public final List<Task> viewedTasksList = new LinkedList<>();

    @Override
    public void addViewedTask(Task task) {
        int listSize = viewedTasksList.size();
        if(listSize == 10) {
            viewedTasksList.remove(0);
            System.out.println("Список просмотренных задач заполнен, для сохранения новой задачи удалена самая старая из сохранённых задач");
        }
        System.out.println("Искомая задача с id " + task.getId() + " сохранена в списке просмотренных задач");
        viewedTasksList.add(task);
    }

    public void printAddViewedTask(Task task) {
        addViewedTask(task);
        System.out.println("Искомая задача с id " + task.getId() + " сохранена в списке просмотренных задач");
    }

    @Override
    public List<Task> getHistory() {
        if(!viewedTasksList.isEmpty()) {
            System.out.println("Список просмотренных задач:");
            for (int i = 0; i < viewedTasksList.size(); i++) {
                Task task = viewedTasksList.get(i);
                System.out.println("Задача " + task.name);
            }
        } else {
            System.out.println("Список просмотренных задач вывести невозможно, задачи отсутствуют!");
        }
        return viewedTasksList;
    }

}