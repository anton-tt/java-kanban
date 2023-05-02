package server;

import basic.Endpoint;
import typeAdapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private final Gson gson;
    TaskManager taskManager;

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

    //private void taskHandler(HttpExchange httpExchange)


    public void start() {
        System.out.println("Запускаем сервер httpServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("httpServer приостановлен на " + PORT + " порту!");
    }


    /*public static void main(String[] args) throws IOException {


        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        // httpTaskServer.stop();
    }*/

    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /tasks/ запроса от клиента.");
            //try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            System.out.println("path   " + path);
            System.out.println("method   " + method);
            System.out.println("query   " + query);

            Endpoint endpoint = getEndpoint(method, path, query);
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

            /*}
            } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();*/


        }


        private Endpoint getEndpoint(String method, String path, String query) {
            String[] splitStrings = path.split("/");
            //System.out.println("QQQQsplitStrings   " + splitStrings[2]);
            /*if(splitStrings.length > 2) {
                String typeTask = splitStrings[2]; */
            String typeTask = null;



            switch (method) {
                case "GET":
                    if (splitStrings.length == 2) {
                        return Endpoint.GET_TASKS_ALL;
                    } else if ((splitStrings.length > 2)) {
                        typeTask = splitStrings[2];
                    switch (typeTask) {
                        case "task":
                            /*if(splitStrings.length == 3 && query == null) {
                                return Endpoint.GET_TASKS;
                            } else {*/
                            return Endpoint.GET_TASK;
                        // }
                        case "epic":
                            /*if(splitStrings.length == 3 && query == null) {
                                return Endpoint.GET_EPICS;
                            } else {*/
                            return Endpoint.GET_EPIC;
                        //}
                        case "subtask":
                           /* if(splitStrings.length == 3 && query == null) {
                                return Endpoint.GET_SUBTASKS;
                            } else {*/
                            return Endpoint.GET_SUBTASK;
                        //}
                        case "history":
                            return Endpoint.GET_HISTORY;
                        case "prioritized":
                            return Endpoint.GET_PRIORITIZED;

                    }}

                case "POST":
                    if (splitStrings.length > 2) {
                        typeTask = splitStrings[2];
                        System.out.println("QQQQsplitStrings   " + typeTask);
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
                    } else if (splitStrings.length == 2) {
                        return Endpoint.UNKNOWN;
                    }

                case "DELETE":
                    if (splitStrings.length == 2) {
                        return Endpoint.DELETE_TASKS_ALL;
                    } else if ((splitStrings.length > 2)) {
                        typeTask = splitStrings[2];


                    switch (typeTask) {
                        case "task":
                           /* if (splitStrings.length == 3 && query == null) {
                                return Endpoint.DELETE_TASKS;
                            } else {*/
                            return Endpoint.DELETE_TASK;
                        // }
                        case "epic":
                           /* if (splitStrings.length == 3 && query == null) {
                                return Endpoint.DELETE_EPICS;
                            } else {*/
                            return Endpoint.DELETE_EPIC;
                        // }
                        case "subtask":
                            /*if (splitStrings.length == 3 && query == null) {
                                return Endpoint.DELETE_SUBTASKS;
                            } else { */
                            return Endpoint.DELETE_SUBTASK;
                        }
                    }
                default:
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
            System.out.println("newTaskId " + newTaskId );

            Task oldTask = taskManager.discoverTask(newTaskId);
            System.out.println("oldTask " + oldTask );
            if (/*taskId >= 0*/ oldTask != null) {
                taskManager.updateTask(oldTask, newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  обновлена", 201);
                //return;
            } else if (oldTask == null) {
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
            /*EpicTask oldTask = (EpicTask) taskManager.discoverTask(newTaskId);
            if( oldTask != null ) {
                taskManager.updateEpictask(oldTask, newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  обновлена", 201);
                //return;
            } else if (oldTask == null) {*/
            taskManager.putNewEpictaskInMap(newTask);
            writeResponse(exchange, "Эпик с id=" + newTaskId + " создан", 201);
            return;
            //}
            // writeResponse(exchange, "Задача с id=" + newTaskId + "  не добавлена, наложение времени", 201);
        }

        private void handlePostUpdateSubtask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            SubTask newTask = gson.fromJson(body, SubTask.class);
            int newTaskId = newTask.getId();
            SubTask oldTask = (SubTask) taskManager.discoverTask(newTaskId);
            if (/*taskId >= 0*/ oldTask != null) {
                taskManager.updateSubtask(oldTask, newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  обновлена", 201);
                //return;
            } else if (oldTask == null) {
                taskManager.putNewSubtaskInMap(newTask);
                writeResponse(exchange, "Задача с id=" + newTaskId + "  добавлена", 201);
                return;
            }
            writeResponse(exchange, "Задача с id=" + newTaskId + "  не добавлена, наложение времени", 201);
        }
    }
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
/*


    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            // приводим localDate к необходимому формату
            jsonWriter.value(localDateTime.format(formatterWriter));
        }
        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatterWriter);
        }
    }
    static class DurationAdapter extends TypeAdapter<Duration> {


        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            // приводим localDate к необходимому формату
            jsonWriter.value(duration.toMinutes());
        }
        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        }

    }*/
}

   /* static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(formatterWriter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatterWriter);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        }
    }*/







       /* private String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), UTF_8);
        }


        private void sendText(HttpExchange h, String text) throws IOException {
            byte[] resp = text.getBytes(UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charsets=utf-8");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);*/
        //}
   /* private void sendAchievement(HttpExchange h, int rCode) throws IOException {
        System.out.println("Запрос исполнен!");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(rCode, 0);
    }*/

       /* private boolean hasId(HttpExchange h) {
            String rawQuery = h.getRequestURI().getRawQuery();
            return rawQuery != null && (rawQuery.contains("id="));
        }

        private int getId(HttpExchange h) {
            String rawQuery = h.getRequestURI().getRawQuery();
            String substringId = rawQuery.substring("id=".length());
            int id = Integer.parseInt(substringId);
            return id;
        }


   /* class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String requestMethod = httpExchange.getRequestMethod();

                switch (requestMethod) {
                    case "GET": {
                        System.out.println("GET-запрос по пути ");
                        handleGet(httpExchange);
                        break;
                    }
                    case "POST": {
                        System.out.println("POST-запрос по пути ");
                        handlePost(httpExchange);
                        break;
                    }
                    case "DELETE": {
                        System.out.println("DELETE-запрос по пути ");
                        handleDelete(httpExchange);
                        break;
                    }
                    default:
                        System.out.println("Ждём один из трёх методов: GET, POST или DELETE. Получили " + requestMethod);
                        //writeResponse(exchange, "Такого эндпоинта не существует", 404);
                        httpExchange.sendResponseHeaders(405, 0);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }*/

       /* private void handleGet(HttpExchange httpExchange) throws IOException {
            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");
            //  /tasks/task 0 - ; 1 - tasks; 2 -  или typeTask;
            String typeTask = splitStrings[1];
            if (typeTask == null) {
                System.out.println("/tasks/");
                String jsonString = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(httpExchange, jsonString);
                System.out.println("Список задач, отсортированных в приоритетном порядке, передан!");
                return;
            } else if (typeTask != null && hasId(httpExchange)) {
                int taskId = getId(httpExchange);

                switch (typeTask) {
                    case "task":
                        System.out.println("/tasks/task?id=" + taskId);
                        taskManager.getRequiredTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Задача с id " + taskId + " передана!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "epic":
                        System.out.println("/tasks/epic?id=" + taskId);
                        taskManager.getRequiredTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Эпик с id " + taskId + " передан!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "subtask":
                        System.out.println("/tasks/subtask?id=" + taskId);
                        taskManager.getRequiredTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Подзадача с id " + taskId + " передана!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                }
            } else if (typeTask != null && !hasId(httpExchange)) {
                switch (typeTask) {
                    case "task":
                        System.out.println("/tasks/task/");
                    case "epic":
                        System.out.println("/tasks/epic/");
                    case "subtask":
                        System.out.println("/tasks/subtask/");
                }


            }
       /* private void handlePost(HttpExchange httpExchange) {


        }*/

      //  }

       /* private void handleDelete(HttpExchange httpExchange) throws IOException {
            //System.out.println("Запрос начинает исполняться!");
            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");
            //  /tasks/task 0 - ; 1 - tasks; 2 -  или typeTask;
            String typeTask = splitStrings[1];
            if (typeTask == null) {
                System.out.println("/tasks/");
                taskManager.clearMapTasks();
                //sendAchievement(httpExchange, 200);
                System.out.println("Все имеющиеся задачи удалены!");
                httpExchange.sendResponseHeaders(200, 0);
            } else if (typeTask != null && hasId(httpExchange)) {
                int taskId = getId(httpExchange);

                switch (typeTask) {
                    case "task":
                        System.out.println("/tasks/task?id=" + taskId);
                        taskManager.removeTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Задача с id " + taskId + " удалена!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "epic":
                        System.out.println("/tasks/epic?id=" + taskId);
                        taskManager.removeTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Эпик с id " + taskId + " удалён!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "subtask":
                        System.out.println("/tasks/subtask?id=" + taskId);
                        taskManager.removeTask(taskId);
                        //sendAchievement(httpExchange, 200);
                        System.out.println("Подзадача с id " + taskId + " удалена!");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                }
            }


        }


    }

}




   /* public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер приостановлен на " + PORT + " порту!");
    }

    /*private Endpoint getEndpoint(String requestPath, String requestMethod) {

        Endpoint endpoint = null;
        String[] splits = requestPath.split("/");
        if(splits[1].equals("posts") && (splits.length == 2) && requestMethod.equals("GET")) {
            endpoint = Endpoint.GET_POSTS;
        } else if((splits.length == 4) && splits[3].equals("comments") && requestMethod.equals("GET")) {
            endpoint = Endpoint.GET_COMMENTS;
        }
        else if((splits.length == 4) && splits[3].equals("comments") && requestMethod.equals("POST")) {
            endpoint = Endpoint.POST_COMMENT;
        } else {
            endpoint = Endpoint.UNKNOWN;
        }

        return endpoint;
    }*/






   /* HttpTaskManager httpTaskManager = Managers.getDefault("http://localhost:8078");



        /*httpServer.createContext("/tasks/task/", new TaskHandler(httpTaskManager));
       /* httpServer.createContext("/tasks/subtask/", new SubtaskHandler(httpTaskManager));
        httpServer.createContext("/tasks/epic/", new EpictaskHandler(httpTaskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskEpictaskHandler(httpTaskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(httpTaskManager));
        httpServer.createContext("/tasks/", new TasksHandler(httpTaskManager));*/


/* private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private final TaskManager manager;
    private final Gson gson;
     class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case "GET": {
                    handleGet(exchange);
                    break;
                }
                case "POST": {
                    handlePost(exchange);
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange);
                    break;
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }


}
*/
/*
/*{
	"name": "Праздничный обед",
  "description": "Из 9 блюд на 10 персон",
	"id": 0,
  "status": "Status.NEW",
  "startTime": "2023, 5, 1, 12, 00",
   "duration": 240
}
* */