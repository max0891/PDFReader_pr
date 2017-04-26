package pack.main;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pack.pdfsplitter.PDFSplitter;
import pack.routes.SimpleRouteBuilder;
import pack.routes.StopRoute;

public class CamelTransfer {
	public static void main(String args[]){
	    Logger logger = LoggerFactory.getLogger(CamelTransfer.class);
		SimpleRouteBuilder rout = new SimpleRouteBuilder();
		CamelContext camelContext = new DefaultCamelContext();
		
			
		try{
//			ProducerTemplate prod = camelContext.createProducerTemplate();
			
			camelContext.addRoutes(rout);
			camelContext.start();
			camelContext.startAllRoutes();
			camelContext.getShutdownStrategy().setTimeout(2);
			

			Thread.sleep(10000);
			while (!camelContext.getRouteStatus(rout.mainrouteID).isStopped()) {
				Thread.sleep(10000);				
			}

		} catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}	
}