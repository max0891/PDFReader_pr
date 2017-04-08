import com.sun.pdfview.PDFPage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PDFStreamConverter {

    public PDFStreamConverter(PDFPage page) {
        int CurrentPageNumber = page.getPageNumber();
        int width = 2400;
        int height = 2800;

        Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

        Image PDFImage = page.getImage(width, height,
                rect,
                null,
                true,
                true
        );

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bufImageGraphics = bufferedImage.createGraphics();
        bufImageGraphics.drawImage(PDFImage, 0, 0, null);

        SaveIamge(bufferedImage, CurrentPageNumber);
        writeTessText(bufferedImage, CurrentPageNumber);

        //Runtime.getRuntime().gc();

    }

    private void SaveIamge(BufferedImage image, int pagenum) {
        try {
            File ImageFile = new File(PDFReader.OutImageName + String.valueOf(pagenum) + ".png");
            ImageIO.write(image, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTessText(BufferedImage img, int CurrentPageNumber) {
        try {
            String filename = PDFReader.OutTessFileName + CurrentPageNumber + ".txt";
            File outtext = new File(filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(outtext));
            Tesseract tess = new Tesseract();
            tess.setLanguage("eng");
            String text = tess.doOCR(img);
            bw.append(formatText(text));
            bw.close();
            //new MongoTest(outtext);
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatText(String text) {
        StringBuilder result = new StringBuilder();
        StringBuilder garbage = new StringBuilder();
        byte bytes[] = text.getBytes();

        for(int i = 0; i < bytes.length; ++i)
        {
            //[a-zA-z0-9] '.' ','
            if((bytes[i] >= 48 && bytes[i] <= 57) || (bytes[i] >= 65 && bytes[i] <= 90) || (bytes[i] >= 97 && bytes[i] <= 122) || (bytes[i] == 32)  || (bytes[i] == 46) || (bytes[i] == 44) || (bytes[i] == 10)) //not digit
            {
                continue;
            }
            else
                bytes[i] = 32;
        }

        //result.append(new String(bytes));
//        String lines[] = new String(bytes).split("\n");
//        for(String s : lines)
//        {
//            String tmp = s;
//            int spacecount = 0;
//            int len = s.length();
//            for(char c : s.toCharArray())
//            {
//                if(c == ' ')spacecount++;
//            }
//
//            if(len != 0) {
//                float percent = ((float) spacecount / (float) len * 100);
//                if (percent > 30) {
//                    garbage.append(tmp + "\n");
//                    tmp = "";
//                }
//            }
//            spacecount = 0;
//            result.append(tmp + "\n");
//        }

        String words[] = new String(bytes).split(" ");
        for(String s : words)
        {
            String tmp = s;
            if(s.length() == 1) {
                garbage.append(s);
                tmp = "";
            }
            result.append(tmp + " ");
        }

        writeGarbage(garbage.toString());
        return result.toString();
    }

    private synchronized void writeGarbage(String text)
    {
        try {
            PDFReader.garbagewriter.append(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

























