package pack.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import pack.pdfsplitter.PDFSplitter;

public class FileLoaderProcess implements Processor {
	public void process(Exchange exchange) throws Exception {

		System.out.println(exchange.getIn().getBody().toString());

		
	}
}