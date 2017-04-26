package pack.pdfsplitter;
import com.sun.pdfview.PDFFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import pack.bookmarks.PDFBookmarks;
import pack.converterthread.PDFConverterThread;
import pack.routes.SimpleRouteBuilder;
import pack.routes.StopRoute;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class PDFSplitter implements Processor{
	public static final int THREADSNUM = 8;
	private static final Logger logger = LoggerFactory.getLogger(PDFSplitter.class);
//	public void SplitPDFbyPages(Exchange exchange){
	public void process(Exchange exchange) throws Exception{
		
		
		FileInputStream fis;
        Properties property = new Properties();
        fis = new FileInputStream("src/main/resources/filetransfer.properties");
        property.load(fis);
            
		System.out.println(exchange.getIn().getHeaders().toString());
//		
		String filename = exchange.getIn().getHeader("CamelFileNameOnly").toString();
		String filePath = exchange.getIn().getHeader("CamelFileAbsolutePath").toString();
		String outputfolder = filePath.replace(exchange.getIn().getHeader("CamelFileParent").toString(), property.getProperty("outputfolder"));
		String splittedPagesFolder = outputfolder.replace(".pdf","/SplittedPages");
		String bookmarksFolder = outputfolder.replace(".pdf", "/Bookmarks");
		String imagesFolder = outputfolder.replace(".pdf", "/Images");
		String textsFolder = outputfolder.replace(".pdf", "/Texts");
		String garbageFolder = outputfolder.replace(".pdf", "/Garbage");
		
		File allObjects = new File( outputfolder.replace(".pdf", ""));
        if(!allObjects.exists())
            allObjects.mkdir();
        File SplittedPages = new File(splittedPagesFolder);
        if(!SplittedPages.exists())
        	SplittedPages.mkdir();
        File Bookmarks = new File(bookmarksFolder);
        if(!Bookmarks.exists())
        	Bookmarks.mkdir();
        File Images = new File(imagesFolder);
        if(!Images.exists())
        	Images.mkdir();
        File Texts = new File(textsFolder);
        if(!Texts.exists())
        	Texts.mkdir();
        File GarbageFolder = new File(garbageFolder);
        if(!GarbageFolder.exists())
        	GarbageFolder.mkdir();
        
		PdfReader reader = null;
		
        Map<String, String> map = new HashMap<String,String>();
        try {
            reader = new PdfReader(filePath);
            int n = reader.getNumberOfPages();
            int i = 1;
            while (i <= n) {
                String outFile = splittedPagesFolder + "/" + filename.replace(".pdf", String.format("_%d", i )) + ".pdf";            	
                
                Document document = new Document(reader.getPageSizeWithRotation(1));
                PdfCopy writer = new PdfCopy(document, new FileOutputStream(outFile));
                document.open();

                PdfImportedPage page = writer.getImportedPage(reader, i);
                writer.addPage(page);

                map.put(Integer.toString(i),outFile);

                document.close();
                writer.close();
                ++i;
            }
            reader.close();

        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }
        Pages = new HashMap<String, String>(map);
        map.clear();
        
        PDFBookmarks bookmarks = new PDFBookmarks(filePath, bookmarksFolder + "/Bookmarks.txt", Pages);
      
        try {
        	File garbageOutText = new File(GarbageFolder + "/Garbage.txt");
        	BufferedWriter garbagewriter = new BufferedWriter(new FileWriter(garbageOutText));

        
        	RandomAccessFile raf = new RandomAccessFile(filePath, "r");
        	FileChannel channel = raf.getChannel();
        	ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        
			PDFFile pdffile = new PDFFile(buf);
		
			ArrayList<Thread> threads = new ArrayList<Thread>(THREADSNUM);
            IntStream.range(0, THREADSNUM).parallel().forEach((i) -> threads.add(new PDFConverterThread(pdffile, imagesFolder, textsFolder, filename, garbagewriter)));

            ForkJoinPool forkJoinPool = new ForkJoinPool(THREADSNUM);

            forkJoinPool.submit(() -> threads.stream().parallel().forEach((i) -> i.start())).get();
            forkJoinPool.submit(() -> threads.stream().parallel().forEach((i) -> 
            		{
                        try {
                            i.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    })).get();

            garbagewriter.close();            
            channel.close();
            raf.close();
			
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(),e);
		}
		System.out.println("SPLITTER DONE");
        
//        try {
//			exchange.getContext().stopRoute(SimpleRouteBuilder.routeID);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//        StopRoute stopRoute = new StopRoute(SimpleRouteBuilder.mainrouteID);
//        try {
//			stopRoute.stopRoute(exchange);
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//		}
    }
	
	

    private PdfReader reader;
    private PdfWriter writer;
    private String inputFileName;
    private String outFilepath;
    private String dir;
    private HashMap<String,String> Pages = null;
}
