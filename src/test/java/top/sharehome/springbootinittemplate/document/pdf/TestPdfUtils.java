package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.BarcodeHorizontal;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.BarcodeType;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.ImageExtension;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.ImageHorizontal;

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
        String readable = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/readable.pdf";
        String image = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/image.png";
        String outputZip = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/imageZip.zip";
        String outputTxt = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/pdf/file/paragraphTxt.txt";
        try {
            // 生成一个待读取的PDF文档
            new PdfUtils.Writer()
                    .addPage()
                    .addWatermark("测试水印")
                    .addSplitLine()
                    .addParagraph(
                            new PdfUtils.PdfParagraph()
                                    .setTextContent("测试段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落段落")
                                    .setTextIndent(2)
                    )
                    .addSplitLine()
                    .addParagraph("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
                    .addSplitLine()
                    .addTable(
                            new String[][]{
                                    {"测试表头", "测试表头", "测试表头"},
                                    {"测试表体", "测试表体"},
                                    {"测试表体"},
                                    {"测试表尾", "测试表尾", "测试表尾"}}, true, true
                    )
                    .addSplitLine()
                    .addImage(new FileInputStream(image), ImageExtension.PNG, ImageHorizontal.CENTER)
                    .addSplitLine()
                    .addBarcode("https://github.com/AntonyCheng/spring-boot-init-template", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详情")
                    .addSplitLine()
                    .doWrite(new FileOutputStream(readable));
            // 输出图像ZIP
            new PdfUtils.Reader(new FileInputStream(readable)).getImagesZip(new FileOutputStream(outputZip));
            // 输出段落TXT
            new PdfUtils.Reader(new FileInputStream(readable)).getParagraphsTxt(new FileOutputStream(outputTxt));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
