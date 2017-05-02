package pack.converterthread;
import net.sourceforge.tess4j.Tesseract;
import pack.mongo.MongoTest;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFConverterThread extends Thread{
	public final int WIDTH = 2400;
    public final int HEIGHT = 2800;
	public static int CurrentPage = 0;
    private int curpage;
    private PDFFile pdffile = null;
    private String outImageFolder;
    private String outTextFolder;
    private String pdffilename;
    private BufferedWriter garbagewriter = null;
    ArrayList<Integer> unhandledPages;
    private static final Logger logger = LoggerFactory.getLogger(PDFConverterThread.class);
    
    public PDFConverterThread(PDFFile pdffile, String outImageFolder, String outTextFolder, String pdffilename, BufferedWriter garbagewriter)
    {
        unhandledPages = new ArrayList<Integer>();
        this.pdffile = pdffile;
        this.outImageFolder = outImageFolder;
        this.outTextFolder = outTextFolder; 
        this.pdffilename = pdffilename;
        this.garbagewriter = garbagewriter;
    }

    @Override
    public void run() {
            BufferedImage bufferedImage = null;
            Graphics2D bufImageGraphics = null;
            Rectangle rect = null;
            Image pdfimage = null;
            try {
                while (UpdateCurrentPage() <= pdffile.getNumPages()) {
                    rect = new Rectangle(0, 0, (int) pdffile.getPage(curpage).getBBox().getWidth(), (int) pdffile.getPage(curpage).getBBox().getHeight());
                    PDFPage page = pdffile.getPage(curpage);

                    pdfimage = page.getImage(WIDTH, HEIGHT, rect,null,true,true);
                    bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
                    bufImageGraphics = bufferedImage.createGraphics();
                    bufferedImage.createGraphics().drawImage(pdfimage, 0, 0, null);


                    saveIamge(bufferedImage, curpage);
                    writeTessText(bufferedImage, curpage);

                    page = null;
                    pdfimage = null;
                    rect = null;
                    bufferedImage = null;
                    bufImageGraphics = null;
                    Runtime.getRuntime().gc();
                }

                if(unhandledPages.size() > 0)
                {
                    for(Integer i : unhandledPages) {                        
                        rect = new Rectangle(0, 0, (int) pdffile.getPage(i).getBBox().getWidth(), (int) pdffile.getPage(i).getBBox().getHeight());
                        PDFPage page = pdffile.getPage(i);
                        pdfimage = page.getImage(WIDTH, HEIGHT, rect, null, true, true);

                        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
                        bufImageGraphics = bufferedImage.createGraphics();
                        bufferedImage.createGraphics().drawImage(pdfimage, 0, 0, null);

                        saveIamge(bufferedImage, i);
                        writeTessText(bufferedImage, i);

                        page = null;
                        pdfimage = null;
                        rect = null;
                        bufferedImage = null;
                        bufImageGraphics = null;
                    }
                }

                Thread.currentThread().interrupt();
            } catch (OutOfMemoryError oome) {
                logger.error("image number = " + curpage + " Memory total = " + Runtime.getRuntime().totalMemory());
                logger.error(oome.getMessage());
                unhandledPages.add(curpage);
                run();
            }


    }

    private void saveIamge(BufferedImage image, int pagenum) {
    	
        String imagefilename = outImageFolder + "/" +pdffilename.replace(".pdf", String.format("_%d", pagenum)) + ".png";
        try {
            File ImageFile = new File(imagefilename);
            ImageIO.write(image, "png", ImageFile);
            ImageFile = null;
            logger.debug("Image " + imagefilename + " successfully saved");

        } catch (IOException e) {
        	 logger.error(e.getMessage());            
        }
    }


    private void writeTessText(BufferedImage img, int pagenum) {
        try {
            String filename = outTextFolder + "/" + pdffilename.replace(".pdf", String.format("_%d", pagenum)) + ".txt";
            File outtext = new File(filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outtext));
            
            Tesseract tess = new Tesseract();
            tess.setLanguage("eng");
            String text = tess.doOCR(img);
            bw.append(formatText(text, filename));
            bw.close();

            bw = null;
            tess = null;

            new MongoTest(outtext);
        } catch (Exception e) {
        	 logger.error(e.getMessage());
        } 
    }

    private String formatText(String text, String filename) {

        StringBuilder result = new StringBuilder();
        StringBuilder garbage = new StringBuilder();
        garbage.append("\n" + filename + "\n");
        byte bytes[] = text.getBytes();

        for(int i = 0; i < bytes.length; ++i)
        {
            //[a-zA-z0-9] '.' ',' '(' ')' ' '
            if((bytes[i] >= 48 && bytes[i] <= 57) ||
                    (bytes[i] >= 65 && bytes[i] <= 90) ||
                    (bytes[i] >= 97 && bytes[i] <= 122) ||
                    (bytes[i] == 32)  ||
                    (bytes[i] == 46) ||
                    (bytes[i] == 44) ||
                    (bytes[i] == 10) ||
                    (bytes[i] == 40) ||
                    (bytes[i] == 41)) //not digit
            {
                continue;
            }
            else {
                garbage.append((char)bytes[i]);
                bytes[i] = (byte)32;

            }
        }

        String ClearSpecial = new String(bytes).replaceAll(" +", " ");

        String lines[] = ClearSpecial.split("\n");
        for(String s : lines)
        {
            String tmp = s.replaceAll("\\s+", " ");
            int spacecount = 0;
            int len = s.length();


            if(len > 1) {
                for(char c : s.toCharArray())
                {
                    if(c == ' ')spacecount++;
                }

                float percent = ((float) spacecount / (float) len * 100);
                if (percent > 30) {
                    garbage.append(tmp + "\n");
                    tmp = "";
                }
            }

            spacecount = 0;
            result.append(tmp + "\n");
        }

        writeGarbage(garbage.toString());
        garbage = null;
        bytes = null;
        text = null;
        return result.toString();
    }

    private synchronized void writeGarbage(String text)
    {
        try {
            garbagewriter.append(text);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized int UpdateCurrentPage()
    {
        curpage = ++CurrentPage;
        return curpage;
    }
}
