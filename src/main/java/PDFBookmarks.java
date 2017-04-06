import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Валерия on 03.04.2017.
 */
public class PDFBookmarks {
    private String InputFileName;
    private String OutFileName;
    private List<HashMap<String, Object>> bookmarks;
    private Map Pages;



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
            e.printStackTrace();
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
            e.printStackTrace();
        }

    }

    private void WriteBookmarksToFile(HashMap<String, Object> bm, BufferedWriter bw) {
        try {
            String number = (String) bm.get("Page");
            bw.append((String)bm.get("Title") + "   " + Pages.get(number.substring(0,number.indexOf(" "))) + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<HashMap<String,Object>> kids = (List<HashMap<String,Object>>)bm.get("Kids");
        if (kids != null) {
            for (int i = 0; i < kids.size(); i++) {
                WriteBookmarksToFile(kids.get(i),bw);
            }
        }
    }
}
