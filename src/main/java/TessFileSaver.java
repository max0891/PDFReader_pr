import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class TessFileSaver {

    TessFileSaver(ArrayList <BufferedImage> list, String OutFileName)
    {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(OutFileName));
            Tesseract tess = Tesseract.getInstance();
            tess.setLanguage("eng");

            for (BufferedImage i : list)
            {
                String data = null;
                try {
                   data = tess.doOCR(i);
                } catch (TesseractException e) {
                    e.printStackTrace();
                }
                bw.append(data);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
