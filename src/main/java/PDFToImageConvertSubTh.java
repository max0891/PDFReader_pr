import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class PDFToImageConvertSubTh extends Thread {

    private String OutFilename;
    private int cur_page;
    private String OutTessFileName;
    private Image img = null;
    private File ImageFile = null;
    private BufferedWriter bw;


    public PDFToImageConvertSubTh(String OutFilename, String  OutTessFileName)
    {
        this.OutFilename = OutFilename;
        this.OutTessFileName = OutTessFileName;
    }
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (PDFToImageConvertMultiTh.currrent_page <= PDFToImageConvertMultiTh.numPgs){
            PDFPage page = PDFToImageConvertMultiTh.getPage();
            cur_page = page.getPageNumber();

            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

            img = page.getImage(rect.width, rect.height,
                    rect,
                    null,
                    true,
                    true
            );
            writeTessText((BufferedImage) img);
            try {
                ImageFile = new File(OutFilename  + String.valueOf(cur_page) + ".png");
                ImageIO.write((BufferedImage) img, "png", ImageFile);
                Runtime.getRuntime().gc();
            } catch (IOException e) {
                e.printStackTrace();}
        }
        try {
            bw = new BufferedWriter(new FileWriter(OutTessFileName));

            for( String text : PDFToImageConvertMultiTh.stringlist )
                bw.append(text);

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        long time = finish - start;
        System.out.println(time);

    }

//    private static synchronized void update_time()
//    {
//        total_time += finish - start;
//        System.out.println(total_time);
//    }

    private synchronized void writeTessText(BufferedImage img)
    {
       try {
           Tesseract tess = new Tesseract();
           tess.setLanguage("eng");
           String text = tess.doOCR(img);
           PDFToImageConvertMultiTh.stringlist.add(cur_page - 1,text);
        }  catch (TesseractException e) {
           e.printStackTrace();
       }
    }


}
