package top.sharehome.springbootinittemplate.utils.document.pdf;

import lombok.extern.slf4j.Slf4j;
import org.dromara.pdf.pdfbox.core.base.Document;
import org.dromara.pdf.pdfbox.core.base.Page;
import org.dromara.pdf.pdfbox.core.component.Textarea;

import java.util.ArrayList;

/**
 * PDF工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class PdfUtils {

    /**
     * 输出PDF模板内部类
     */
    public static class Template {

    }

    /**
     * 写入PDF数据内部类
     */
    public class Writer {

        private Document document;

        private ArrayList<PdfPage> pages;

        private class PdfPage {

            private Page page;

            private ArrayList<Textarea> textareaList;

        }

    }

    /**
     * 读取PDF数据内部类
     */
    public static class Reader {

    }

}
