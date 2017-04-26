package pack.routes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pack.mongo.MongoTest;
import pack.pdfsplitter.PDFSplitter;
import pack.routes.*;

public class SimpleRouteBuilder extends RouteBuilder {
	public static final String mainrouteID = "pdfroute";
	public static final String completionrouteID = "completionroute";
    private static final Logger logger = LoggerFactory.getLogger(SimpleRouteBuilder.class);

	@Override
	public void configure() throws Exception {
		FileInputStream fis;
        Properties property = new Properties();
 
        try {
            fis = new FileInputStream("src/main/resources/filetransfer.properties");
            property.load(fis);
            from(property.getProperty("source") + property.getProperty("inputfolder") + property.getProperty("inOptions"))
			.routeId(mainrouteID)
			.onCompletion().bean(new StopRoute(mainrouteID)).to(property.getProperty("source") + property.getProperty("outputfolder")).end()
			.process(new PDFSplitter())			
			.to(property.getProperty("source") + property.getProperty("outputfolder"));
            
        }catch (IOException e) {
        	logger.error(e.getMessage(),e);
        }
	}

}
