package testtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import testtask.models.ImageData;
import testtask.models.ImageList;


public class RequestHandler {
    final String imagesPath = "http://interview.agileengine.com/images";

    HttpClient httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_2)
//                .connectTimeout(Duration.ofSeconds(10))
            .build();

    ObjectMapper jsonMapper = new ObjectMapper();

    URI authUri = URI.create("http://interview.agileengine.com/auth");


    public String getAuthToken() throws IOException, InterruptedException {

        HttpRequest authRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"apiKey\": \"23567b218376f79d9415\"}"))
                .uri(authUri)
                .build();

        HttpResponse<String> response = this.httpClient.send(authRequest, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200){
            System.out.println(response.statusCode());
            throw new IOException("unauthorised");
        }

        Map<String,String> body = this.jsonMapper.readValue(response.body(),new TypeReference<>(){});

        String token = body.get("token");
        return token;
    }

    public ImageList getImagesList(int page, String authToken) throws IOException, InterruptedException {
        HttpRequest imagesRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(imagesPath + "?page=" + page))
                .header("Authorization", "Bearer " + authToken)
                .build();

        HttpResponse<String> response = this.httpClient.send(imagesRequest, BodyHandlers.ofString());
        ImageList images = this.jsonMapper.readValue(response.body(), ImageList.class);

        return images;
    }

    public ImageData getImageData(String imageId, String authToken) throws IOException, InterruptedException {
        HttpRequest imageDataRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(imagesPath + "/" + imageId))
                .header("Authorization", "Bearer " + authToken)
                .build();

        HttpResponse<String> imageResponse = this.httpClient.send(imageDataRequest, BodyHandlers.ofString());
        ImageData imageData = this.jsonMapper.readValue(imageResponse.body(), ImageData.class);
        return imageData;
    }
}
