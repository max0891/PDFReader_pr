package pack.main;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

import pack.routes.SimpleRouteBuilder;

public class CamelTransfer {
	public static void main(String args[]){
		SimpleRouteBuilder rout = new SimpleRouteBuilder();
		CamelContext ctx = new DefaultCamelContext();
		try{
			ctx.addRoutes(rout);
			ctx.getShutdownStrategy().setSuppressLoggingOnTimeout(true);
			ctx.start();
		
			Thread.sleep(100000);
			
			ctx.stop();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}