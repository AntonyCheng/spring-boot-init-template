package top.sharehome.springbootinittemplate.document.pdf;

import org.dromara.pdf.pdfbox.core.enums.HorizontalAlignment;
import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 直接使用main函数对PdfUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPdfUtils {

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/test.pdf";
        String logoPath = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/logo.png";
        try (FileOutputStream fileOutputStream = new FileOutputStream(path); FileInputStream fileInputStream = new FileInputStream(logoPath)) {
            new PdfUtils.PdfPage();
            new PdfUtils.Writer()
                    .addPage(new PdfUtils.PdfPage(null, null, null, null, null))
                    .addTextarea("Hello World")
                    .addTextarea((List<String>) null, null, null, null, null, null)
                    .addBlank()
                    .addSplitLine()
                    .addTextarea(new PdfUtils.PdfTextarea().setTextList(List.of("Hello World")))
                    .addPage()
                    .addTextarea("你好 世界")
                    .addImage(fileInputStream, HorizontalAlignment.CENTER, 0.5f)
                    .addSplitLine(Color.blue, 10f, true, 1f, 1f)
                    .addTextarea("嘻嘻")
                    .addTextarea(List.of("1", "2", "3"))
                    .savePdf(fileOutputStream);
        } catch (IOException ignore) {

        }
    }

}
