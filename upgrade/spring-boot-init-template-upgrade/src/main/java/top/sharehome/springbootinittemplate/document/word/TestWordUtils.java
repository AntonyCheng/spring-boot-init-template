package top.sharehome.springbootinittemplate.document.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 直接使用main函数对WordUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestWordUtils {

    private static final String READABLE_WORD_PATH_NAME = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/word/file/readable.docx";

    private static final String OUTPUT_ZIP_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/word/file/temp.docx";

    public static void main(String[] args) {
        try (XWPFDocument word = WordUtils.Writer.createWord(); FileOutputStream outputStream = new FileOutputStream(OUTPUT_ZIP_FILE_PATH_NAME)) {
            WordUtils.Writer.addParagraph(word, "Hello World, 你好世界！", WordUtils.ParagraphDetails.builder().setIsBold(true).build());
            WordUtils.Writer.addBreakPage(word);
            WordUtils.Writer.addParagraph(word, "Hello World, 你好世界！");
            FileInputStream fileInputStream = new FileInputStream(READABLE_WORD_PATH_NAME);
            Collection<byte[]> values = WordUtils.Reader.getPicturesArray(fileInputStream).values();
            for (byte[] value : values) {
                WordUtils.Writer.addPicture(word, new ByteArrayInputStream(value));
            }
            Map<Integer, WordUtils.TableMap> tableMaps = WordUtils.Reader.getTablesText(new FileInputStream(READABLE_WORD_PATH_NAME));
            WordUtils.TableMap tableMap = tableMaps.get(0);
            WordUtils.Writer.addTable(word, tableMap);
            WordUtils.Writer.doWrite(word, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
