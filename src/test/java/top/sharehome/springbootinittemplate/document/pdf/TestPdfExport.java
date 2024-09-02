package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.ExportDataSource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直接使用main函数对PdfUtils进行导出测试
 *
 * @author AntonyCheng
 */
public class TestPdfExport {

    private static final String EXPORT_THYMELEAF_WORD_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/export-thymeleaf.pdf";

    private static final String EXPORT_FREEMARKER_WORD_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/export-freemarker.pdf";

    private static final String EXPORT_JTE_WORD_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/export-jte.pdf";

    public static void main(String[] args) {
        try (
                FileOutputStream exportThymeleafStream = new FileOutputStream(EXPORT_THYMELEAF_WORD_FILE_PATH_NAME);
                FileOutputStream exportFreemarkerStream = new FileOutputStream(EXPORT_FREEMARKER_WORD_FILE_PATH_NAME);
                FileOutputStream exportJteStream = new FileOutputStream(EXPORT_JTE_WORD_FILE_PATH_NAME)
        ) {
            Map<String, Object> freemarkerData = new HashMap<>();
            List<String> freemarkerList = new ArrayList<>(2);
            freemarkerList.add("hello");
            freemarkerList.add("world");
            freemarkerData.put("list", freemarkerList);
            freemarkerData.put("str", "hello world");
            Map<String, Object> thymeleafData = new HashMap<>();
            thymeleafData.put("data", "hello world");
            Map<String, Object> jteData = new HashMap<>();
            List<String> jteList = new ArrayList<>(2);
            jteList.add("hello");
            jteList.add("world");
            jteData.put("list", jteList);
            jteData.put("str", "hello world");
            new PdfUtils.Template().export("template-freemarker.fo", ExportDataSource.FREEMARKER, freemarkerData, exportFreemarkerStream);
            new PdfUtils.Template().export("template-thymeleaf.fo", ExportDataSource.THYMELEAF, thymeleafData, exportThymeleafStream);
            new PdfUtils.Template().export("template-jte.jte", ExportDataSource.JTE, jteData, exportJteStream);
        } catch (IOException e) {
            System.out.println("意外错误");
        }
    }

}
