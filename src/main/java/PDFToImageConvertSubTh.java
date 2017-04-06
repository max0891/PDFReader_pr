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


            int width = 1200;
            int height = 1400;

            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

            img = page.getImage(width, height,
                    rect,
                    null,
                    true,
                    true
            );

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D bufImageGraphics = bufferedImage.createGraphics();
            bufImageGraphics.drawImage(img, 0, 0, null);

            writeTessText(bufferedImage);
            try {
                ImageFile = new File(OutFilename  + String.valueOf(cur_page) + ".png");
//                ImageIO.write((BufferedImage) img, "png", ImageFile);
                ImageIO.write(bufferedImage, "png", ImageFile);
                Runtime.getRuntime().gc();
            } catch (IOException e) {
                e.printStackTrace();}
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
           bw = new BufferedWriter(new FileWriter(OutTessFileName + cur_page + ".txt"));
           Tesseract tess = new Tesseract();
           tess.setLanguage("eng");
           bw.append(tess.doOCR(img));
           bw.close();
        }  catch (TesseractException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }


}
