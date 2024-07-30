package top.sharehome.springbootinittemplate.document.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
        try (FileOutputStream outputStream = new FileOutputStream(WRITABLE_WORD_FILE_PATH_NAME)) {
            WordUtils.Writer writer = new WordUtils.Writer()
                    .addParagraph(new WordUtils.WordParagraph().setTextContent("Hello World, 你好世界！").setIsBold(true))
                    .addPage()
                    .addParagraph("Hello World, 你好世界！");
            FileInputStream fileInputStream = new FileInputStream(READABLE_WORD_PATH_NAME);
            // 从READABLE_WORD_PATH_NAME文档中获取图片
            Map<String, List<byte[]>> imagesByteArray = new WordUtils.Reader(fileInputStream).getImagesByteArray();
            imagesByteArray.values().forEach(list->{
                list.forEach(image->{
                    // 将图片遍历插入WRITABLE_WORD_FILE_PATH_NAME文档中
                    writer.addImage(new ByteArrayInputStream(image));
                });
            });
            // 从READABLE_WORD_PATH_NAME文档中获取第一个表格
            List<WordUtils.WordTable.TableMap> tableMaps = new WordUtils.Reader(new FileInputStream(READABLE_WORD_PATH_NAME)).getTablesMaps();
            WordUtils.WordTable.TableMap tableMap = tableMaps.get(0);
            // 插入WRITABLE_WORD_FILE_PATH_NAME文档表格
            writer.addTable(tableMap, true);
            // 执行最终写入
            writer.doWrite(outputStream);
        } catch (IOException e) {
            System.out.println("意外错误");
        }
    }
}
