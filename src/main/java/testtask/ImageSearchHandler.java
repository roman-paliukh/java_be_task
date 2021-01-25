package testtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import testtask.models.ImageData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

public class ImageSearchHandler implements HttpHandler {
    Dao dao;
    ObjectMapper objectMapper;

    ImageSearchHandler(Dao dao){
        this.dao = dao;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();

        String requestPath = uri.getPath();
        String searchParam = requestPath.substring(1).split("/",2)[1];
        System.out.println(searchParam);

        try {
            List<ImageData> imagesData = dao.readImagesData(searchParam);
            String responseBody = objectMapper.writeValueAsString(imagesData);

            handleResponse(httpExchange, 200, responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            handleResponse(httpExchange, 500, "[]");
        }
        httpExchange.close();
    }

    private void handleResponse(HttpExchange exchange, int status, String body) throws IOException {
        try (OutputStream output = exchange.getResponseBody()){
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length());
            output.write(body.getBytes());
            output.flush();
        }
    }
}