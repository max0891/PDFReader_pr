import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.ArrayList;

public class PDFToImageConvert {

    private String InputFileName;// = "Acura Integra Service Manual 1997.pdf";
    private String ImagesOut;
    private BufferedImage bufferedImage = null;
    private ArrayList<BufferedImage> Images = null;



    public BufferedImage getBufferedImage() {
            return bufferedImage;
    }

    public PDFToImageConvert (String InputFileName, String ImagesOut)
    {
        this.InputFileName = InputFileName;
        this.ImagesOut = ImagesOut;

        Images = new ArrayList<BufferedImage>();
        try {
            setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //saveAsImage();


    }

    public void setup() throws IOException {

        File file = new File(InputFileName);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        PDFFile pdffile = new PDFFile(buf);


        int numPgs = pdffile.getNumPages();

//        numPgs = 10;

        for (int i = 1; i < numPgs; i++) {
            PDFPage page = pdffile.getPage(i);
            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

            Image img = page.getImage(rect.width, rect.height,
                    rect,
                    null,
                    true,
                    true
            );
            bufferedImage = toBufferedImage(img);
            //Images.add(bufferedImage);
            //saveAsImage();
            File ImageFile = new File(ImagesOut + "Image_" + String.valueOf(i) + ".png");
            ImageIO.write(bufferedImage, "png", ImageFile);
        }
    }

    public BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();

        boolean hasAlpha = hasAlpha(image);
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    public void saveAsImages()
    {
        try {
            int i = 1;
            for(BufferedImage bi : Images) {
                File ImageFile = new File(ImagesOut + "Image_" + String.valueOf(i) + ".png");
                ImageIO.write(bi, "png", ImageFile);
                ++i;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}