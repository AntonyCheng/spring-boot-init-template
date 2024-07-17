package top.sharehome.springbootinittemplate.document.pdf;

import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
//                FileInputStream fileInputStream1 = new FileInputStream(logoPath1);
                FileInputStream fileInputStream2 = new FileInputStream(logoPath2);
//                FileInputStream fileInputStream3 = new FileInputStream(path);
        ) {
            PdfUtils.PdfTable pdfTable = new PdfUtils.PdfTable()
                    .setPdfTableHead(
                            new PdfUtils.PdfTable.PdfTableHead().setPdfTableRows(new ArrayList<>() {
                                {
                                    add(new PdfUtils.PdfTable.PdfTableRow().setPdfTableCells(new ArrayList<>() {
                                        {
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小明1"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小红1"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小黑1"));
                                        }
                                    }));
                                }
                            })
                    ).setPdfTableBody(
                            new PdfUtils.PdfTable.PdfTableBody().setPdfTableRows(new ArrayList<>() {
                                {
                                    add(new PdfUtils.PdfTable.PdfTableRow().setPdfTableCells(new ArrayList<>() {
                                        {
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小明2"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小红2"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小黑2"));
                                        }
                                    }));
                                    add(new PdfUtils.PdfTable.PdfTableRow().setPdfTableCells(new ArrayList<>() {
                                        {
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小明3"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小红3"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小黑3"));
                                        }
                                    }));
                                }
                            })
                    ).setPdfTableFooter(
                            new PdfUtils.PdfTable.PdfTableFooter().setPdfTableRows(new ArrayList<>() {
                                {
                                    add(new PdfUtils.PdfTable.PdfTableRow().setPdfTableCells(new ArrayList<>() {
                                        {
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小明4"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小红4"));
                                            add(new PdfUtils.PdfTable.PdfTableCell().setCellContent("小黑4"));
                                        }
                                    }));
                                }
                            })
                    );
            new PdfUtils.Writer()
                    .addPage()
                    .addSplitLine()
                    .addParagraph(new PdfUtils.PdfParagraph().setTextContent("我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你").setTextIndent(2))
                    .addParagraph(new PdfUtils.PdfParagraph().setTextContent("我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你").setTextIndent(2))
                    .addSplitLine()
                    .addParagraph(new PdfUtils.PdfParagraph().setTextContent("我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你我爱你").setFontSize(24).setTextIndent(2).setUnderLineColor(Color.orange).setDeleteLineColor(Color.blue))
                    .addSplitLine()
                    .addParagraph("123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World")
                    .addSplitLine()
                    .addImage(new PdfUtils.PdfImage().setPath(logoPath1).setImageHorizontal(ImageHorizontal.CENTER).setHeight(300))
                    .addSplitLine()
                    .addParagraph("123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World123456你好世界 Hello World", 20, Color.red, FontWeight.BOLDER, FontStyle.ITALIC)
                    .addSplitLine()
                    .addImage(fileInputStream2, ImageExtension.PNG, ImageHorizontal.CENTER, null, 100)
                    .addSplitLine()
                    .addTable(pdfTable)
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
