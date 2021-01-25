package testtask.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageData {
    public String id;
    public String author;
    public String camera;
    public String tags;

    @JsonProperty("cropped_picture")
    public String croppedPicture;

    @JsonProperty("full_picture")
    public String fullPicture;

    public ImageData(){}

    public ImageData(String id, String author, String camera, String tags,
                     String croppedPicture, String fullPicture) {
        this.id = id;
        this.author = author;
        this.camera = camera;
        this.tags = tags;
        this.croppedPicture = croppedPicture;
        this.fullPicture = fullPicture;
    }
}
