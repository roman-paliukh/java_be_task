package testtask;

import testtask.models.ImageData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Dao {
    final String TABLE = "images_data";

    final String createStr = "INSERT INTO " + TABLE +
            "(id, author, camera, tags, cropped_picture, full_picture ) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    final String readStr = "SELECT * FROM " + TABLE +
            " WHERE author=? OR camera=? OR tags LIKE ?";

    Connection connection;
    PreparedStatement createStm;
    PreparedStatement readStm;

    public Dao(String dbPath) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
    }

    public void initDB() throws SQLException {
        try(Statement stmt = this.connection.createStatement()){
            String sqlString = "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(image_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " id TEXT NOT NULL, " +
                    " author TEXT NOT NULL, " +
                    " camera TEXT, " +
                    " tags TEXT, " +
                    " cropped_picture TEXT, " +
                    " full_picture TEXT) ";

            stmt.executeUpdate(sqlString);
        }

        createStm = connection.prepareStatement(createStr);
        readStm = connection.prepareStatement(readStr);
    }

    public void clearData() throws SQLException {
        try(Statement stmt = this.connection.createStatement()){
            String sqlString = "DELETE FROM "+ TABLE+";VACUUM";
            stmt.execute(sqlString);
        }
    }

    public void createImagesData(Collection<ImageData> images) throws SQLException {

        for(ImageData image : images){
            createStm.setString(1, image.id);
            createStm.setString(2, image.author);
            createStm.setString(3, image.camera);
            createStm.setString(4, image.tags);
            createStm.setString(5, image.croppedPicture);
            createStm.setString(6, image.fullPicture);
            createStm.addBatch();
        }
        createStm.executeBatch();
    }

    public List<ImageData> readImagesData(String filterParam) throws SQLException {
        readStm.setString(1, filterParam);
        readStm.setString(2, filterParam);
        readStm.setString(3, "%#" +filterParam+" %");

        ResultSet result = readStm.executeQuery();
        List<ImageData> imagesData = new ArrayList<>();
        while (result.next()){
            imagesData.add(new ImageData(
                    result.getString("id"),
                    result.getString("author"),
                    result.getString("camera"),
                    result.getString("tags"),
                    result.getString("cropped_picture"),
                    result.getString("full_picture")
            ));
        }

        return imagesData;
    }
}
