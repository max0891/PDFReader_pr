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
			ctx.getShutdownStrategy().setTimeout(5);
//			ctx.getShutdownStrategy().setSuppressLoggingOnTimeout(true);
			ctx.start();
			ctx.startRoute("pdfroute");
		
			Thread.sleep(100000);
			while (!ctx.getRouteStatus("pdfroute").isStopped())
			{
				Thread.sleep(10000);
			}
			ctx.stop();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}