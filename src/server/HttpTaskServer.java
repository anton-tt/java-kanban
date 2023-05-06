package server;

import basic.Endpoint;
import typeAdapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/", new TaskHandler()/*this::handle*/);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = Managers.getDefault();
    }

    public void start() {
        System.out.println("Запускаем сервер httpServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("httpServer приостановлен на " + PORT + " порту!");
    }

    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /tasks/ запроса от клиента.");
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            System.out.println("path   " + path);
            System.out.println("method   " + method);
            System.out.println("query   " + query);

            Endpoint endpoint = getEndpoint(method, path);
            System.out.println("Эндпоинт   " + endpoint);
            switch (endpoint) {
                case GET_TASK:
                    handleGetTask(exchange);
                    break;
                case GET_EPIC:
                    handleGetEpic(exchange);
                    break;
                case GET_SUBTASK:
                    handleGetSubtask(exchange);
                    break;
                case GET_TASKS_ALL:
                    handleGetAllTasks(exchange);
                    break;
                case GET_HISTORY:
                    handleGetTasksHistory(exchange);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritizedTasks(exchange);
                    break;

                case POST_UPDATE_TASK:
                    handlePostUpdateTask(exchange);
                    break;
                case POST_UPDATE_EPIC:
                    handlePostUpdateEpic(exchange);
                    break;
                case POST_UPDATE_SUBTASK:
                    handlePostUpdateSubtask(exchange);
                    break;

                case DELETE_TASK:
                    handleDeleteTask(exchange);
                    break;
                case DELETE_EPIC:
                    handleDeleteEpic(exchange);
                    break;
                case DELETE_SUBTASK:
                    handleDeleteSubtask(exchange);
                    break;
                case DELETE_TASKS_ALL:
                    handleDeleteAllTasks(exchange);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private Endpoint getEndpoint(String method, String path) {
            String[] splitStrings = path.split("/");
            if (method.equals("GET")) {
                return getEndpointGET(splitStrings);
            } else if (method.equals("POST")) {
                return getEndpointPOST(splitStrings);
            } else if (method.equals("DELETE")) {
                return getEndpointDEL(splitStrings);
            } else {
                return Endpoint.UNKNOWN;
            }
        }

        private Endpoint getEndpointGET(String[] strings) {
            if (strings.length == 2) {
                return Endpoint.GET_TASKS_ALL;
            } else if ((strings.length > 2)) {
                String typeTask = strings[2];
                switch (typeTask) {
                    case "task":
                        return Endpoint.GET_TASK;
                    case "epic":
                        return Endpoint.GET_EPIC;
                    case "subtask":
                        return Endpoint.GET_SUBTASK;
                    case "history":
                        return Endpoint.GET_HISTORY;
                    case "prioritized":
                        return Endpoint.GET_PRIORITIZED;
                    default:
                        return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        }

        private Endpoint getEndpointPOST(String[] strings) {
            if (strings.length > 2) {
                String typeTask = strings[2];
                switch (typeTask) {
                    case "task":
                        return Endpoint.POST_UPDATE_TASK;
                    case "epic":
                        return Endpoint.POST_UPDATE_EPIC;
                    case "subtask":
                        return Endpoint.POST_UPDATE_SUBTASK;
                    default:
                        return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        }

        private Endpoint getEndpointDEL(String[] strings) {
            if (strings.length == 2) {
                return Endpoint.DELETE_TASKS_ALL;
            } else if (strings.length > 2) {
                String typeTask = strings[2];
                switch (typeTask) {
                    case "task":
                        return Endpoint.DELETE_TASK;
                    case "epic":
                        return Endpoint.DELETE_EPIC;
                    case "subtask":
                        return Endpoint.DELETE_SUBTASK;
                    default:
                        return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        private Optional<Integer> getTaskId(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            String substringId = query.substring("id=".length()); //&&&&&&&&&&&&&&&&&
            try {
                return Optional.of(Integer.parseInt(substringId));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private void handleGetTask(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            Task task = taskManager.getRequiredTask(taskId);
            if (task != null) {
                writeResponse(exchange, gson.toJson(task), 200);
                return;
            }
            writeResponse(exchange, "Задача с идентификатором " + taskId + " не найдена", 404);
        }

        private void handleGetEpic(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            EpicTask epic = (EpicTask) taskManager.getRequiredTask(taskId);
            if (epic != null) {
                writeResponse(exchange, gson.toJson(epic), 200);
                return;
            }
            writeResponse(exchange, "Эпик с идентификатором " + taskId + " не найден", 404);
        }

        private void handleGetSubtask(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            SubTask subtask = (SubTask) taskManager.getRequiredTask(taskId);
            if (subtask != null) {
                writeResponse(exchange, gson.toJson(subtask), 200);
                return;
            }
            writeResponse(exchange, "Подзадача с идентификатором " + taskId + " не найдена", 404);
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            List<Task> allTasksList = taskManager.getListTasks();
            if (allTasksList != null) {
                writeResponse(exchange, gson.toJson(allTasksList.toString()), 200);
                return;
            }
            writeResponse(exchange, "Задачи отсутствуют, список не может быть сформирован", 404);
        }

        private void handleGetTasksHistory(HttpExchange exchange) throws IOException {
            List<Task> historyList = taskManager.getHistory();
            if (historyList != null) {
                writeResponse(exchange, gson.toJson(historyList.toString()), 200);
                return;
            }
            writeResponse(exchange, "Просмотренные задачи отсутствуют, история не может быть выведена", 404);
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            List<Task> prioritizedTasksList = taskManager.getPrioritizedTasks();
            if (prioritizedTasksList != null) {
                writeResponse(exchange, gson.toJson(prioritizedTasksList.toString()), 200);
                return;
            }
            writeResponse(exchange, "Просмотренные задачи отсутствуют, история не может быть выведена", 404);
        }

        private void handleDeleteTask(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            Task task = taskManager.discoverTask(taskId);
            if (task != null) {
                taskManager.removeTask(taskId);
                writeResponse(exchange, "Задача с идентификатором " + taskId + " удалена", 200);
                return;
            }
            writeResponse(exchange, "Задача с идентификатором " + taskId + " отсутствовала", 404);
        }

        private void handleDeleteEpic(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            EpicTask task = (EpicTask) taskManager.discoverTask(taskId);
            if (task != null) {
                taskManager.removeTask(taskId);
                writeResponse(exchange, "Эпик с идентификатором " + taskId + " удалён", 200);
                return;
            }
            writeResponse(exchange, "Эпик с идентификатором " + taskId + " отсутствовал", 404);
        }

        private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            SubTask task = (SubTask) taskManager.discoverTask(taskId);
            if (task != null) {
                taskManager.removeTask(taskId);
                writeResponse(exchange, "Подзадача с идентификатором " + taskId + " удалена", 200);
                return;
            }
            writeResponse(exchange, "Подзадача с идентификатором " + taskId + " отсутствовала", 404);
        }

        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
            List<Task> allTasksList = taskManager.getListTasks();
            if (allTasksList != null) {
                taskManager.clearMapTasks();
                writeResponse(exchange, "Все задачи удалены", 200);
                return;
            }
            writeResponse(exchange, "Задачи отсутствуют, удалить невозможно", 404);
        }

        private void handlePostUpdateTask(HttpExchange exchange) throws IOException {
            System.out.println("Метод handlePostUpdateTask работает!");

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            System.out.println("body " + body);
            Task newTask = gson.fromJson(body, Task.class);
            System.out.println("newTask " + newTask);
            int newTaskId = newTask.getId();
            System.out.println("newTaskId " + newTaskId);

            Task oldTask = taskManager.discoverTask(newTaskId);
            System.out.println("oldTask " + oldTask);
            if (oldTask != null) {
                taskManager.updateTask(oldTask, newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  обновлена", 201);
                //return;
            } else {
                taskManager.putNewTaskInMap(newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  добавлена", 201);
                return;
            }
            writeResponse(exchange, "Задача с id=" + newTaskId + "  не добавлена, наложение времени", 400);
        }

        private void handlePostUpdateEpic(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            EpicTask newTask = gson.fromJson(body, EpicTask.class);
            int newTaskId = newTask.getId();
            taskManager.putNewEpictaskInMap(newTask);
            writeResponse(exchange, "Эпик с id=" + newTaskId + " создан", 201);
        }

        private void handlePostUpdateSubtask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            SubTask newTask = gson.fromJson(body, SubTask.class);
            int newTaskId = newTask.getId();
            SubTask oldTask = (SubTask) taskManager.discoverTask(newTaskId);
            if (oldTask != null) {
                taskManager.updateSubtask(oldTask, newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  обновлена", 201);
            } else {
                taskManager.putNewSubtaskInMap(newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  добавлена", 201);
                return;
            }
            writeResponse(exchange, "Задача с id=" + newTaskId + "  не добавлена, наложение времени", 201);
        }
    }

}