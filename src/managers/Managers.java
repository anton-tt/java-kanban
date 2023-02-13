package managers;

public class Managers {
    private static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static TaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

}