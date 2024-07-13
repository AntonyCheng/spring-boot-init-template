package top.sharehome.springbootinittemplate.document.pdf;

import org.dromara.pdf.pdfbox.core.enums.HorizontalAlignment;
import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 直接使用main函数对PdfUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPdfUtils {

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/test.pdf";
        String logoPath1 = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/logo.png";
        String logoPath2 = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/logo(横版封面图).png";
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                FileInputStream fileInputStream1 = new FileInputStream(logoPath1);
                FileInputStream fileInputStream2 = new FileInputStream(logoPath2);
                FileInputStream fileInputStream3 = new FileInputStream(path);
        ) {
            new PdfUtils.Writer()
                    .addPage(new PdfUtils.PdfPage().setColor(Color.WHITE))
                    .addTextarea(new PdfUtils.PdfTextarea().setFontColor(Color.BLUE))
                    .addPage(new PdfUtils.PdfPage().setColor(Color.WHITE))
                    .addTextarea(new PdfUtils.PdfTextarea().setFontColor(Color.RED))
                    .doWrite(fileOutputStream);
//            new PdfUtils.Writer()
//                    .addPage()
//                    .addSplitLine()
//                    .addTextarea("你好       世界")
//                    .addSplitLine()
//                    .addPage()
//                    .addSplitLine()
//                    .addTextarea("Hello World")
//                    .addSplitLine()
//                    .addPage()
//                    .addSplitLine()
//                    .addImage(fileInputStream1, HorizontalAlignment.CENTER, 0.5f)
//                    .addSplitLine()
//                    .addPage()
//                    .addSplitLine()
//                    .addImage(fileInputStream2, HorizontalAlignment.CENTER, 0.5f)
//                    .addSplitLine()
//                    .addPage()
//                    .addSplitLine()
//                    .addTextarea("Hello World")
//                    .addSplitLine()
//                    .doWrite(fileOutputStream);

        } catch (IOException ignore) {

        }
    }

}
