import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;

public class PDFToImageConvertMultiTh{
    private String InputFileName;// = "Acura Integra Service Manual 1997.pdf";
    private String ImagesOut;
    private String OutTessFileName;
    private BufferedImage bufferedImage = null;
    private static ArrayList<BufferedImage> Images = null;
    public static volatile int currrent_page = 1;
    private static PDFPage pdfpage;
    public static int numPgs;
    public static BufferedWriter bw;
    public static List<String> stringlist;

    static PDFFile pdffile;

    public PDFToImageConvertMultiTh(String InputFileName,String  OutTessFileName, String ImagesOut)
    {
        this.OutTessFileName = OutTessFileName;
        this.ImagesOut = ImagesOut;
        try {


            File file = new File(InputFileName);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            pdffile = new PDFFile(buf);
            numPgs = pdffile.getNumPages();
            //numPgs = 100;

            PDFToImageConvertSubTh th1 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th2 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th3 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th4 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th5 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th6 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th7 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);
            PDFToImageConvertSubTh th8 = new PDFToImageConvertSubTh(ImagesOut, OutTessFileName);

            th1.start();
            th2.start();
            th3.start();
            th4.start();
            th5.start();
            th6.start();
            th7.start();
            th8.start();
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }

    public static synchronized PDFPage getPage()
    {
        pdfpage = pdffile.getPage(currrent_page++);
        return pdfpage;
    }
}
