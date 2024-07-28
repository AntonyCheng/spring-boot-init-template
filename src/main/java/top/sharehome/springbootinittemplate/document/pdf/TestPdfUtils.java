package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.BarcodeType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 直接使用main函数对PdfUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPdfUtils {

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/pdf/file/test.pdf";
        try {
            new PdfUtils.Writer()
                    .addParagraph("1234567890")
                    .addPage()
                    .addParagraph("a我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123")
                    .addParagraph("b我爱你123我爱你")
                    .addParagraph("c我爱你123我爱你")
                    .addParagraph("d我爱你123我爱你")
                    .addParagraph("e我爱你123我爱你")
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE)
                    .addParagraph("f我爱你123我爱你")
                    .doWrite(new FileOutputStream(path));
//            FileInputStream inputStream1 = new FileInputStream(path);
//            List<String> paragraphList = new PdfUtils.Reader(inputStream1).getParagraphsList(1);
//            FileInputStream inputStream2 = new FileInputStream(path);
//            Map<String, List<byte[]>> imagesByteArray = new PdfUtils.Reader(inputStream2).getImagesByteArray();
//            System.out.println(paragraphList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
