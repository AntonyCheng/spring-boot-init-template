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
import org.dromara.pdf.pdfbox.core.enums.FontStyle;
import org.dromara.pdf.pdfbox.core.enums.HorizontalAlignment;
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

        /**
         * 初始化文档
         */
        private final Document document;

        /**
         * 页面管理
         */
        private final ArrayList<Page> pageList;

        /**
         * 当前页面索引
         */
        private int pageIndex;

        public Writer() {
            // PDFBox存储策略默认仅主存MainMemoryOnly，如果内存有限或者需要长时间重复操作PDF，请选择仅临时文件TempFileOnly或混合模式Mix
            this.document = PdfHandler.getDocumentHandler().create(MemoryPolicy.setupMainMemoryOnly());
            // 设置文档默认字体为SimHei（黑体）
            this.document.setFontName("SimHei");
            this.pageList = new ArrayList<>();
            // 因为是索引，从0开始，所以初始化为-1
            this.pageIndex = -1;
        }

        public Writer(String fontName, MemoryPolicy memoryPolicy) {
            this.document = PdfHandler.getDocumentHandler().create(Objects.isNull(memoryPolicy) ? MemoryPolicy.setupMainMemoryOnly() : memoryPolicy);
            this.document.setFontName(Objects.isNull(fontName) ? "SimHei" : fontName);
            this.pageList = new ArrayList<>();
            this.pageIndex = -1;
        }

        /**
         * 添加页面
         */
        public Writer addPage() {
            return addPage(new PdfPage());
        }

        /**
         * 添加页面
         *
         * @param pdfPage 页面构造类
         */
        public Writer addPage(PdfPage pdfPage) {
            // 创建新页面
            Page page = document.createPage(Objects.isNull(pdfPage.getPageSize()) ? PageSize.A4 : pdfPage.getPageSize());
            // 设置页面默认字体为SimHei（黑体）
            page.setFontName(Objects.isNull(pdfPage.getPageFontName()) ? "SimHei" : pdfPage.getPageFontName());
            // 设置页面默认四周边距为25
            page.setMargin(Objects.isNull(pdfPage.getPageMargin()) ? 25 : pdfPage.getPageMargin());
            // 设置页面默认背景颜色为白色
            page.setBackgroundColor(Objects.isNull(pdfPage.getColor()) ? Color.WHITE : pdfPage.getColor());
            pageList.add(page);
            this.pageIndex++;
            return this;
        }

        /**
         * 添加空行文本内容
         */
        public Writer addBlank() {
            return addTextarea(
                    new PdfTextarea()
                            .setIsBlank(true)
            );
        }

        /**
         * 添加文本内容
         *
         * @param textarea 单段落文本内容
         */
        public Writer addTextarea(String textarea) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(List.of(textarea))
            );
        }

        /**
         * 添加文本内容
         *
         * @param textarea  单段落文本内容
         * @param fontName  字体名称
         * @param fontSize  字体大小
         * @param fontColor 字体颜色
         * @param isBold    是否加粗
         * @param isItalic  是否斜体
         */
        public Writer addTextarea(String textarea, String fontName, Float fontSize, Color fontColor, Boolean isBold, Boolean isItalic) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(List.of(textarea))
                            .setFontName(fontName)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
            );
        }

        /**
         * 添加文本内容
         *
         * @param textareaList 多段落文本内容
         */
        public Writer addTextarea(List<String> textareaList) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(textareaList)
            );
        }

        /**
         * 添加文本内容
         *
         * @param textareaList 多段落文本内容
         * @param fontName     字体名称
         * @param fontSize     字体大小
         * @param fontColor    字体颜色
         * @param isBold       是否加粗
         * @param isItalic     是否斜体
         */
        public Writer addTextarea(List<String> textareaList, String fontName, Float fontSize, Color fontColor, Boolean isBold, Boolean isItalic) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(textareaList)
                            .setFontName(fontName)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
            );
        }

        /**
         * 添加文本内容
         *
         * @param pdfTextarea PDF文本内容构造器
         */
        public Writer addTextarea(PdfTextarea pdfTextarea) {
            Textarea textarea = new Textarea(pageList.get(pageIndex));
            // 设置文本内容
            if (CollectionUtils.isEmpty(pdfTextarea.getTextList()) || pdfTextarea.getIsBlank()) {
                // 设置是换行还是空行
                textarea.setIsWrap(pdfTextarea.getIsBlank());
                textarea.setText(StringUtils.EMPTY);
            } else if (Objects.equals(pdfTextarea.getTextList().size(), 1)) {
                // 设置单段落文本内容
                textarea.setIsWrap(true);
                textarea.setText(pdfTextarea.getTextList().get(0));
            } else {
                // 设置多段落文本内容
                textarea.setIsWrap(true);
                textarea.setTextList(pdfTextarea.getTextList());
            }
            // 设置文本内容字体
            textarea.setFontName(Objects.isNull(pdfTextarea.getFontName()) ? "SimHei" : pdfTextarea.getFontName());
            // 设置文本内容字体大小
            textarea.setFontSize(Objects.isNull(pdfTextarea.getFontSize()) ? 12f : pdfTextarea.getFontSize());
            // 设置文本内容是否加粗或斜体
            if (pdfTextarea.getIsBold() && !pdfTextarea.getIsItalic()) {
                textarea.setFontStyle(FontStyle.BOLD);
            } else if (!pdfTextarea.getIsBold() && pdfTextarea.getIsItalic()) {
                textarea.setFontStyle(FontStyle.ITALIC);
            } else if (pdfTextarea.getIsBold()) {
                textarea.setFontStyle(FontStyle.ITALIC_BOLD);
            }
            // 设置文本内容字体颜色
            textarea.setFontColor(Objects.isNull(pdfTextarea.getFontColor()) ? Color.BLACK : pdfTextarea.getFontColor());
            // 设置文本内容高亮颜色
            if (Objects.nonNull(pdfTextarea.getHighlightColor())) {
                textarea.setIsHighlight(true);
                textarea.setHighlightColor(pdfTextarea.getHighlightColor());
            }
            // 设置文本内容删除线颜色
            if (Objects.nonNull(pdfTextarea.getDeleteLineColor())) {
                textarea.setIsDeleteLine(true);
                textarea.setDeleteLineColor(pdfTextarea.getDeleteLineColor());
            }
            // 设置文本内容下划线颜色
            if (Objects.nonNull(pdfTextarea.getUnderlineColor())) {
                textarea.setIsUnderline(true);
                textarea.setUnderlineColor(pdfTextarea.getUnderlineColor());
            }
            // 设置文本内容字间距
            textarea.setCharacterSpacing(Objects.isNull(pdfTextarea.getCharacterSpacing()) ? 1f : pdfTextarea.getCharacterSpacing());
            // 设置文本内容行间距
            textarea.setLeading(Objects.isNull(pdfTextarea.getLeading()) ? 1f : pdfTextarea.getLeading());
            // 设置文本内容对齐方式默认为左对齐
            textarea.setHorizontalAlignment(Objects.isNull(pdfTextarea.getHorizontalAlignment()) ? HorizontalAlignment.LEFT : pdfTextarea.getHorizontalAlignment());
            // 设置文本内容之间的间距
            textarea.setMarginTop(Objects.isNull(pdfTextarea.getMarginTop()) ? 5f : pdfTextarea.getMarginTop());
            // 熏染文本内容
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

        /**
         * 页面纸张大小，默认为A4
         */
        PageSize pageSize = PageSize.A4;

        /**
         * 页面字体名称，默认"SimHei"（黑体）
         */
        String pageFontName = "SimHei";

        /**
         * 页面四周边距，默认25
         */
        Float pageMargin = 25f;

        /**
         * 页面背景颜色，默认白色
         */
        Color color = Color.WHITE;

    }

    /**
     * PDF文本构造器
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfTextarea {

        /**
         * 文本内容，默认{""}
         */
        private List<String> textList = new ArrayList<>();

        /**
         * 文本内容是否为空行，默认为false，如果设置为true，无论textList为何值，均默认为空行
         */
        private Boolean isBlank = false;

        /**
         * 文本内容字体，默认为"SimHei"（黑体）
         */
        private String fontName = "SimHei";

        /**
         * 文本内容字体大小，默认为12
         */
        private Float fontSize = 12f;

        /**
         * 文本内容字体是否加粗，默认为false
         */
        private Boolean isBold = false;

        /**
         * 文本内容字体是否斜体，默认为false
         */
        private Boolean isItalic = false;

        /**
         * 文本内容字体颜色，默认为黑色
         */
        private Color fontColor = Color.BLACK;

        /**
         * 文本内容高亮颜色
         */
        private Color highlightColor;

        /**
         * 文本内容删除线颜色
         */
        private Color deleteLineColor;

        /**
         * 文本内容下划线颜色
         */
        private Color underlineColor;

        /**
         * 文本内容字间距，默认为1
         */
        private Float characterSpacing = 1f;

        /**
         * 文本内容行间距，默认为1
         */
        private Float leading = 1f;

        /**
         * 文本内容对齐方式，默认为左对齐
         */
        private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

        /**
         * 文本内容之间的间距，默认为5
         */
        private Float marginTop = 5f;

    }

}
