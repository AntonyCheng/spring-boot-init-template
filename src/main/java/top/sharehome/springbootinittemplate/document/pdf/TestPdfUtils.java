package top.sharehome.springbootinittemplate.document.pdf;

import org.dromara.pdf.pdfbox.core.base.Page;
import org.dromara.pdf.pdfbox.core.component.Textarea;
import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                .addPage()
                .addTextarea(List.of("1 2 3\r\n"))
                .addTextarea(List.of("1 2 3\r\n"))
                .addPage()
                .addTextarea(List.of("123","123456"))
                .savePdf(fileOutputStream);
    }

}
