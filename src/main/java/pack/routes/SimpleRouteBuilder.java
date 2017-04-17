package pack.routes;

import org.apache.camel.builder.RouteBuilder;

import pack.pdfsplitter.PDFSplitter;
import pack.process.FileLoaderProcess;

public class SimpleRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:C:/inputFolder?noop=true")
//		.process(new FileLoaderProcess())
		.bean(new PDFSplitter(), "SplitPDFbyPages")
		.to("file:C:/outputFolder");

	}

}
