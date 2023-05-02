package managers;

import server.KVServer;

import java.io.IOException;

public class Managers {
    private static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static TaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefaultBacked() {
        return taskManager;
    }
    public static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

}