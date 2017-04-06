import java.io.IOException;

public class PDFReader {
    public static String filename = "Acura Integra Service Manual 1997.pdf";
    public static String tessfilename = "Acura Integra Service Manual 1997/Acura Integra Service Manual 1997.txt";
    public static String OutFilename = "images/image_";
    public static String dir = "Acura Integra Service Manual 1997/";
    public static String destination = "links";


    public static void main(String[] args) {

//        PDFSplitter P = new PDFSplitter(filename, dir);
//        System.out.println("Splitter has done");
//        long start = System.currentTimeMillis();
//        PDFToImageConvert I = new PDFToImageConvert(filename,"images/");
//        long finish = System.currentTimeMillis();
//        System.out.println(finish - start);
//        System.out.println("PDFToImageConvert has done");
//       PDFBookmarks B = new PDFBookmarks(filename,dir + "bookmarks.txt", P.getPages());
//        System.out.println("Bookmark has done");
//        TessFileSaver tess = new TessFileSaver(I.getImages(),dir + filename.replace(".pdf", ".txt"));
//        System.out.println("TessFileSaver has done");
        PDFToImageConvertMultiTh pdfToImageConvertMultiTh = new PDFToImageConvertMultiTh(filename, tessfilename, OutFilename);
    }

}
