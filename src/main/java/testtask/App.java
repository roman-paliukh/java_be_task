package testtask;

import com.sun.net.httpserver.HttpServer;
import testtask.models.ImageData;
import testtask.models.ImageList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App implements Runnable {
    private RequestHandler requestHandler;
    private Dao dao;
    private long timeInterval;
    private TimeUnit timeUnit;

    public App(RequestHandler requestHandler, Dao dao, long timeInterval, TimeUnit timeUnit) {
        this.requestHandler = requestHandler;
        this.dao = dao;
        this.timeInterval = timeInterval;
        this.timeUnit = timeUnit;
    }

    void fillDB() throws IOException, InterruptedException, SQLException {

        dao.clearData();

        String authToken = requestHandler.getAuthToken();
        int page = 1;
        ImageList images;
        do{
            images = requestHandler.getImagesList(page, authToken);

            List<ImageData> imagesData = images.pictures.parallelStream().map(imageId -> {
                try {
                    return requestHandler.getImageData(imageId.id, authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).filter(p->p!=null).collect(Collectors.toList());
            dao.createImagesData(imagesData);
            System.out.println(String.format("Stored %s values from page %s", imagesData.size(), page));

            ++page;
        }while (images.hasMore);
    }

    @Override
    public void run(){
        Runnable refillDbTask = () -> {
            try {
                this.fillDB();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(refillDbTask, 0, timeInterval, timeUnit);

        try {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
            server.createContext("/search/", new ImageSearchHandler(dao));
            server.setExecutor(threadPoolExecutor);
            server.start();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
