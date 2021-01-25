package testtask.models;

import java.util.List;

public class ImageList {
    public static class ImageId{
        public String id;
        public String cropped_picture;
    }

    public List<ImageId> pictures;
    public int page;
    public int pageCount;
    public boolean hasMore;
}
