package top.sharehome.springbootinittemplate.utils.document.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.pdf.pdfbox.core.base.Document;
import org.dromara.pdf.pdfbox.core.base.MemoryPolicy;
import org.dromara.pdf.pdfbox.core.base.Page;
import org.dromara.pdf.pdfbox.core.base.PageSize;
import org.dromara.pdf.pdfbox.core.component.Textarea;
import org.dromara.pdf.pdfbox.handler.PdfHandler;

import java.awt.*;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public class Template {

    }

    /**
     * 写入PDF数据内部类
     */
    public static class Writer {

        private final Document document;

        private final ArrayList<Page> pageList;

        private int pageIndex;

        public Writer() {
            this.document = PdfHandler.getDocumentHandler().create(MemoryPolicy.setupMainMemoryOnly());
            this.document.setFontName("SimHei");
            this.pageList = new ArrayList<>();
            this.pageIndex = -1;
        }

        public Writer(String fontName, MemoryPolicy memoryPolicy) {
            this.document = PdfHandler.getDocumentHandler().create(Objects.isNull(memoryPolicy) ? MemoryPolicy.setupMainMemoryOnly() : memoryPolicy);
            this.document.setFontName(Objects.isNull(fontName) ? "SimHei" : fontName);
            this.pageList = new ArrayList<>();
            this.pageIndex = -1;
        }

        public Writer addPage() {
            addPage(PageSize.A4, "SimHei", 25, Color.WHITE);
            return this;
        }

        private void addPage(PageSize pageSize, String pageFontName, float pageMargin, Color color) {
            Page page = document.createPage(pageSize);
            page.setFontName(pageFontName);
            page.setMargin(pageMargin);
            page.setBackgroundColor(color);
            pageList.add(page);
            this.pageIndex++;
        }

        public Writer addTextarea(List<String> textList) {
            Textarea textarea = new Textarea(pageList.get(pageIndex));
            if (CollectionUtils.isEmpty(textList)) {
                textarea.setText(StringUtils.EMPTY);
            } else if (Objects.equals(textList.size(), 1)) {
                textarea.setText(textList.get(0));
            } else {
                textarea.setTextList(textList);
            }
            textarea.render();
            return this;
        }

        public void savePdf(OutputStream outputStream) {
            document.appendPage(pageList);
            document.save(outputStream);
            document.close();
        }

    }

    /**
     * 读取PDF数据内部类
     */
    public class Reader {

    }

    /**
     * PDF页面构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(setterPrefix = "set")
    public static class PdfPage {
        PageSize pageSize;
        String pageFontName;
        Float pageMargin;
        Color color;
    }

}
