package pack.routes;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pack.converterthread.PDFConverterThread;
import pack.pdfsplitter.PDFSplitter;

public class StopRoute  { 
    
    private String routeId;	
    private Thread stop; 
    private Logger logger = LoggerFactory.getLogger(StopRoute.class);
    
    public StopRoute(String routeId) { 
            super(); 
            this.routeId = routeId; 
    } 
    
    public StopRoute() {} 

    public void stopRoute(final Exchange exchange) throws Exception { 
            final CamelContext camelContext = exchange.getContext();
            
            System.out.println("STOPPING ROUTE");
              camelContext.getInflightRepository().remove(exchange);        
              camelContext.getShutdownStrategy().setTimeout(2);
              camelContext.stopRoute(routeId);
//              camelContext.removeRouteDefinitions(routeId)
              camelContext.stop();
            }	
    }





