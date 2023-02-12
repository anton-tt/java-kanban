package managers;

public class Managers {
    private static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static TaskManager taskManager = new InMemoryTaskManager();

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

}