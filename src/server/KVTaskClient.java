package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final String uriString;
    private final String apiToken;

    public KVTaskClient(String uriString) throws IOException, InterruptedException {
        this.uriString = uriString;
        client = HttpClient.newHttpClient();
        URI uri = URI.create(uriString+ "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() == 200) {
            apiToken = response.body();
        } else {
            System.out.println("Ошибка при получении токена. Код " + response.statusCode());
            apiToken = null;
        }
    }

    public void put(String key, String json) {
        try {
            URI uri = URI.create(uriString + "/save" + key + "?API_TOKEN=" + apiToken);
            HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код ответа: " + response.statusCode());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка при выполнении запроса");
        }
       }

    public String load(String key) {
        try {
            URI uri = URI.create(uriString + "/load" + key + "?API_TOKEN=" + apiToken);
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код ответа: " + response.statusCode());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при выполнении запроса");
        } catch (NullPointerException e) {
            System.out.println("Тело ответа пустое");
        } return null;
    }

}