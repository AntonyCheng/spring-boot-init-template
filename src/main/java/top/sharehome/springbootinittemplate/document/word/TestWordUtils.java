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

    private static final String WRITABLE_WORD_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/word/file/temp.docx";

    public static void main(String[] args) {
        // 创建Word
        try (XWPFDocument word = WordUtils.Writer.createWord(); FileOutputStream outputStream = new FileOutputStream(WRITABLE_WORD_FILE_PATH_NAME)) {
            // 插入WRITABLE_WORD_FILE_PATH_NAME文档段落
            WordUtils.Writer.addParagraph(word, "Hello World, 你好世界！", new WordUtils.WordParagraph().setIsBold(true));
            // 插入WRITABLE_WORD_FILE_PATH_NAME文档新的一页
            WordUtils.Writer.addBreakPage(word);
            // 插入WRITABLE_WORD_FILE_PATH_NAME文档段落
            WordUtils.Writer.addParagraph(word, "Hello World, 你好世界！");
            FileInputStream fileInputStream = new FileInputStream(READABLE_WORD_PATH_NAME);
            // 从READABLE_WORD_PATH_NAME文档中获取图片
            Collection<byte[]> values = WordUtils.Reader.getPicturesArray(fileInputStream).values();
            for (byte[] value : values) {
                // 将图片遍历插入WRITABLE_WORD_FILE_PATH_NAME文档中
                WordUtils.Writer.addPicture(word, new ByteArrayInputStream(value));
            }
            // 从READABLE_WORD_PATH_NAME文档中获取第一个表格
            Map<Integer, WordUtils.TableMap> tableMaps = WordUtils.Reader.getTablesText(new FileInputStream(READABLE_WORD_PATH_NAME));
            WordUtils.TableMap tableMap = tableMaps.get(0);
            // 插入WRITABLE_WORD_FILE_PATH_NAME文档表格
            WordUtils.Writer.addTable(word, tableMap);
            // 执行最终写入
            WordUtils.Writer.doWrite(word, outputStream);
        } catch (IOException e) {
            System.out.println("意外错误");
        }
    }
}
