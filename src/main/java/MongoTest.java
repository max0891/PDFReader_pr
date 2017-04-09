import com.mongodb.*;
import com.mongodb.gridfs.GridFS;

import java.io.*;


public class MongoTest {
    public MongoTest(File file) {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("admin");

        try {
            new GridFS(db, "documents").createFile(new FileInputStream(file), file.getName()).save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}