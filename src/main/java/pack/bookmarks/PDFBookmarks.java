package pack.bookmarks;
import com.itextpdf.text.pdf.PdfReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.text.pdf.SimpleBookmark;

import pack.pdfsplitter.PDFSplitter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFBookmarks {
	private String InputFileName;
    private String OutFileName;
    private List<HashMap<String, Object>> bookmarks;
    private Map Pages;
    private static final Logger logger = LoggerFactory.getLogger(PDFBookmarks.class);


    public PDFBookmarks(String inputFileName, String outFileName,Map<String, String> pages ) {
        InputFileName = inputFileName;
        OutFileName = outFileName;
        Pages = pages;
        FetchBookmarks();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(OutFileName));
            for(int i = 0; i < bookmarks.size(); ++i)
                WriteBookmarksToFile(bookmarks.get(i),bw);
            bw.close();
        } catch (IOException e) {
        	logger.error(e.getMessage(),e);
        }

    }

    private void FetchBookmarks()
    {
        PdfReader reader = null;
        try {
            reader = new PdfReader(InputFileName);
            bookmarks = SimpleBookmark.getBookmark(reader);
            reader.close();
        } catch (IOException e) {
        	logger.error(e.getMessage(),e);
        }

    }

    private void WriteBookmarksToFile(HashMap<String, Object> bm, BufferedWriter bw) {
        try {
            String number = (String) bm.get("Page");
            bw.append((String)bm.get("Title") + "   " + Pages.get(number.substring(0,number.indexOf(" "))) + '\n');
        } catch (IOException e) {
        	logger.error(e.getMessage(),e);
        }

        List<HashMap<String,Object>> kids = (List<HashMap<String,Object>>)bm.get("Kids");
        if (kids != null) {
            for (int i = 0; i < kids.size(); i++) {
                WriteBookmarksToFile(kids.get(i),bw);
            }
        }
    }
}
