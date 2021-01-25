package testtask;

import com.sun.net.httpserver.HttpServer;
import testtask.models.ImageData;
import testtask.models.ImageList;

import java.net.InetSocketAddress;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {


    public static void main(String args[]){
        long timeInterval = 1;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        if (args.length > 0){
            try {
                timeInterval = Long.parseLong(args[0]);
            }catch (NumberFormatException e){
                System.out.println("can't get time interval from " + args[0]);
            }
        }
        if (args.length >1){
            try {
                timeUnit = TimeUnit.valueOf(args[1].toUpperCase());
            }catch (IllegalArgumentException e){
                System.out.println("can't get time unit from " + args[1]);
            }
        }

        System.out.println(String.format("Will update cache every %s %s", timeInterval, timeUnit));

        try {
            Dao dao = new Dao(":memory:");
            dao.initDB();

            RequestHandler requestHandler = new RequestHandler();

            App app = new App(requestHandler, dao, timeInterval, timeUnit);

            app.run();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(1);
        }
    }
}

