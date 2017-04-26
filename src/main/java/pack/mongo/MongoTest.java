package pack.mongo;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;

import pack.pdfsplitter.PDFSplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;


public class MongoTest {
    private static final Logger logger = LoggerFactory.getLogger(MongoTest.class);

    public MongoTest(File file) {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("admin");

        try {
            new GridFS(db, "documents").createFile(new FileInputStream(file), file.getName()).save();
        } catch (FileNotFoundException e) {
        	logger.error(e.getMessage(),e);
        }

    }
}