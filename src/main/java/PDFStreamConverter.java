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

        Image pdfimage = page.getImage(width, height,
                rect,
                null,
                true,
                true
        );

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D bufImageGraphics = bufferedImage.createGraphics();
        bufImageGraphics.drawImage(pdfimage, 0, 0, null);

        SaveIamge(bufferedImage, CurrentPageNumber);
        writeTessText(bufferedImage, CurrentPageNumber);

        bufferedImage = null;
        bufImageGraphics = null;
        pdfimage = null;

        Runtime.getRuntime().gc();
        Thread.currentThread().interrupt();

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
            bw.append(formatText(text, filename));
            bw.close();

            tess = null;

            new MongoTest(outtext);
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

//        String words[] = ClearSpecial.split(" ");
//        for(String s : words)
//        {
//            String tmp = s;
//            if(s.length() == 1 && s != "a" && s.matches("[-+]?\\d+")) {
//                garbage.append(s);
//                tmp = "";
//            }
//            result.append(tmp + " ");
//        }

        writeGarbage(garbage.toString());
        garbage = null;
        bytes = null;
        text = null;
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

























