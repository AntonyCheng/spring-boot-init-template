package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 直接使用main函数对PdfUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPdfUtils {

    public static void main(String[] args) throws IOException {
        String path = "D:\\SSMLearning\\project-code\\spring-boot-init-template\\src\\main\\java\\top\\sharehome\\springbootinittemplate\\document\\pdf\\file\\test.pdf";
//        Files.createFile(Path.of(path));
        FileOutputStream fileOutputStream = new FileOutputStream(path);
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
                .addImage(new PdfUtils.PdfImage())
                .addSplitLine(Color.blue, 10f)
                .addTextarea("嘻嘻")
                .addTextarea(List.of("1", "2", "3"))
                .savePdf(fileOutputStream);
    }

}
