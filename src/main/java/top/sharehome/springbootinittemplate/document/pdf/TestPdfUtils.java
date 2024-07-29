package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.BarcodeHorizontal;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.BarcodeType;

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
        try {
            new PdfUtils.Writer()
                    .addPage()
                    .addWatermark("伍程成", "202051020123")
                    .addParagraph(new PdfUtils.PdfParagraph().setTextContent("我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123").setTextIndent(2).setFontSize(15))
                    .addPage()
                    .addWatermark(new PdfUtils.PdfWatermark().setTextArray("伍程成", "202051020123"))
                    .addParagraph(new PdfUtils.PdfParagraph().setTextContent("我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123我爱你我爱你123").setLeading(3).setMargin(5))
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addBarcode("https://www.baidu.com", BarcodeType.QR_CODE, BarcodeHorizontal.CENTER, "扫码查看详细信息", 10)
                    .addParagraph("c我爱你123我爱你")
                    .addSplitLine()
                    .addParagraph("d我爱你123我爱你")
                    .addParagraph("e我爱你123我爱你")
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
