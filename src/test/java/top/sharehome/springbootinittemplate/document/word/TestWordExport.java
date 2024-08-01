package top.sharehome.springbootinittemplate.document.word;

import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * 直接使用main函数对WordUtils进行导出测试
 *
 * @author AntonyCheng
 */
public class TestWordExport {

    private static final String EXPORT_WORD_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/word/file/export.docx";

    public static void main(String[] args) {
        try (FileOutputStream exportStream = new FileOutputStream(EXPORT_WORD_FILE_PATH_NAME)) {
            HashMap<String, Object> exportData = new HashMap<>() {
                {
                    put("title", "致用户的一封信");
                    put("name", "测试用户");
                    put("date", LocalDate.now().toString());
                }
            };
            new WordUtils.Template().export("template.docx", exportData, exportStream);
        } catch (IOException e) {
            System.out.println("意外错误");
        }
    }

}
