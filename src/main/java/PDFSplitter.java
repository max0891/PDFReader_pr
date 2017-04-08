import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class PDFSplitter {

    public void setInFile(String inFile) {
        this.inputFileName = inFile;
    }

    public void setOutFile(String outFile) {
        this.outFilepath = outFile;
    }

    public String getInFile() {
        return inputFileName;
    }

    public String getOutFile() {
        return outFilepath;
    }

    public PDFSplitter()
    {

    }

    public Map<String, String> getPages() {
        return Pages;
    }

    public PDFSplitter(String inputFileName, String dir) {
        this.inputFileName = inputFileName;
        this.dir = dir;
        SplitPDFbyPages();
    }

    private void SplitPDFbyPages()
    {
        Map map = new HashMap<String,String>();
        try {
            PdfReader reader = new PdfReader(inputFileName);
            int n = reader.getNumberOfPages();
            int i = 1;
            while (i <= n) {
                String outFile = dir + inputFileName.substring(0, inputFileName.indexOf(".pdf"))
                        + "_" + String.format("%04d", i ) + ".pdf";
                File f = new File(dir);
                if(!f.exists())
                    f.mkdir();
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
            e.printStackTrace();
        }
        Pages = new HashMap<String, String>(map);
        map.clear();
    }

    private PdfReader reader;
    private PdfWriter writer;
    private String inputFileName;
    private String outFilepath;
    private String dir;
    private HashMap<String,String> Pages = null;
}
