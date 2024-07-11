package top.sharehome.springbootinittemplate.utils.document.pdf;

import lombok.AllArgsConstructor;
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
import org.dromara.pdf.pdfbox.core.component.Image;
import org.dromara.pdf.pdfbox.core.component.SplitLine;
import org.dromara.pdf.pdfbox.core.component.Textarea;
import org.dromara.pdf.pdfbox.core.enums.FontStyle;
import org.dromara.pdf.pdfbox.core.enums.HorizontalAlignment;
import org.dromara.pdf.pdfbox.handler.PdfHandler;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;

import java.awt.*;
import java.io.InputStream;
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
    public static class Template {

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
            // 设置页面默认字体大小为12
            page.setFontSize(Objects.isNull(pdfPage.getPageFontSize()) ? 12 : pdfPage.getPageFontSize());
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
         * 添加文本内容和其他组件的间隔
         */
        private void addComponentInterval(Float intervalSize) {
            addTextarea(
                    new PdfTextarea()
                            .setFontSize(intervalSize)
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
                            .setTextList(Objects.nonNull(textarea) ? List.of(textarea) : null)
            );
        }

        /**
         * 添加文本内容
         *
         * @param textarea  单段落文本内容
         * @param fontColor 字体颜色
         * @param isBold    是否加粗
         * @param isItalic  是否斜体
         */
        public Writer addTextarea(String textarea, Color fontColor, Boolean isBold, Boolean isItalic) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(Objects.nonNull(textarea) ? List.of(textarea) : null)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
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
                            .setTextList(Objects.nonNull(textarea) ? List.of(textarea) : null)
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
         * @param fontColor    字体颜色
         * @param isBold       是否加粗
         * @param isItalic     是否斜体
         */
        public Writer addTextarea(List<String> textareaList, Color fontColor, Boolean isBold, Boolean isItalic) {
            return addTextarea(
                    new PdfTextarea()
                            .setTextList(textareaList)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
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
            if (Objects.isNull(pdfTextarea)) {
                log.error("处理PDF文件出错，PdfTextarea参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfTextarea参数为空");
            }
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
            pdfTextarea.setIsBold(Objects.isNull(pdfTextarea.getIsBold()) ? Boolean.FALSE : pdfTextarea.getIsBold());
            pdfTextarea.setIsItalic(Objects.isNull(pdfTextarea.getIsItalic()) ? Boolean.FALSE : pdfTextarea.getIsItalic());
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
            // 设置文本内容之间的边距
            textarea.setMarginTop(Objects.isNull(pdfTextarea.getMargin()) ? textarea.getFontSize() / 2 : pdfTextarea.getMargin());
            // 熏染文本内容
            textarea.render();
            return this;
        }

        /**
         * 添加分割线
         */
        public Writer addSplitLine() {
            return addSplitLine(
                    new PdfSplitLine()
            );
        }

        /**
         * 添加分割线
         *
         * @param color     分割线颜色
         * @param lineWidth 分割线宽度
         */
        public Writer addSplitLine(Color color, Float lineWidth) {
            return addSplitLine(
                    new PdfSplitLine()
                            .setLineColor(color)
                            .setLineWidth(lineWidth)
            );
        }

        /**
         * 添加分割线
         *
         * @param color         分割线颜色
         * @param lineWidth     分割线宽度
         * @param isDotted      分割线是否为点线
         * @param dottedLength  点线长度
         * @param dottedSpacing 点线间隔
         */
        public Writer addSplitLine(Color color, Float lineWidth, Boolean isDotted, Float dottedLength, Float dottedSpacing) {
            return addSplitLine(
                    new PdfSplitLine()
                            .setLineColor(color)
                            .setLineWidth(lineWidth)
                            .setIsDotted(isDotted)
                            .setDottedLength(dottedLength)
                            .setDottedSpacing(dottedSpacing)
            );
        }

        /**
         * 添加分割线
         *
         * @param pdfSplitLine PDF分割线构造器
         */
        public Writer addSplitLine(PdfSplitLine pdfSplitLine) {
            if (Objects.isNull(pdfSplitLine)) {
                log.error("处理PDF文件出错，PdfSplitLine参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfSplitLine参数为空");
            }
            // 设置分割线上边距
            addComponentInterval(Objects.isNull(pdfSplitLine.getMargin()) ? 10f : pdfSplitLine.getMargin());
            Page page = pageList.get(pageIndex);
            SplitLine splitLine = new SplitLine(page);
            splitLine.setIsWrap(true);
            // 设置分割线颜色
            splitLine.setLineColor(Objects.isNull(pdfSplitLine.getLineColor()) ? Color.BLACK : pdfSplitLine.getLineColor());
            // 设置分割线长度
            splitLine.setLineLength(Objects.isNull(pdfSplitLine.getLineLength()) ? page.getWidth() - page.getMarginLeft() - page.getMarginRight() : pdfSplitLine.getLineLength());
            // 设置分割线宽度
            splitLine.setLineWidth(Objects.isNull(pdfSplitLine.getLineWidth()) ? 1f : pdfSplitLine.getLineWidth());
            // 判断是否需要点线
            if (pdfSplitLine.getIsDotted()) {
                // 设置点线长度
                splitLine.setDottedLength(Objects.isNull(pdfSplitLine.getLineLength()) ? 1f : pdfSplitLine.getDottedLength());
                // 设置点线间隔
                splitLine.setDottedSpacing(Objects.isNull(pdfSplitLine.getDottedSpacing()) ? 0f : pdfSplitLine.getDottedSpacing());
            }
            // 渲染分割线
            splitLine.render();
            // 设置分割线下边距
            addComponentInterval(Objects.isNull(pdfSplitLine.getMargin()) ? 10f : pdfSplitLine.getMargin());
            return this;
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像对象流
         */
        public Writer addImage(InputStream inputStream) {
            return addImage(
                    new PdfImage()
                            .setInputStream(inputStream)
            );
        }

        /**
         * 添加图像
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         */
        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment) {
            return addImage(
                    new PdfImage()
                            .setInputStream(inputStream)
                            .setHorizontalAlignment(horizontalAlignment)
            );
        }

        /**
         * 天机图象
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         * @param scale               图像缩放比例
         */
        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment, Float scale) {
            return addImage(
                    new PdfImage()
                            .setInputStream(inputStream)
                            .setHorizontalAlignment(horizontalAlignment)
                            .setScale(scale)
            );
        }

        /**
         * 添加图像
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         * @param width               图像宽度
         * @param height              图像高度
         */
        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment, Integer width, Integer height) {
            return addImage(
                    new PdfImage()
                            .setInputStream(inputStream)
                            .setHorizontalAlignment(horizontalAlignment)
                            .setWidth(width)
                            .setHeight(height)
            );
        }

        /**
         * 添加图像
         *
         * @param pdfImage PDF图像构造器
         */
        public Writer addImage(PdfImage pdfImage) {
            if (Objects.isNull(pdfImage)) {
                log.error("处理PDF文件出错，PdfImage参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数为空");
            }
            // 设置图像上边距
            addComponentInterval(Objects.isNull(pdfImage.getMargin()) ? 10f : pdfImage.getMargin());
            Page page = pageList.get(pageIndex);
            Image image = new Image(page);
            image.setIsWrap(true);
            // 设置图像对象流
            if (Objects.isNull(pdfImage.getInputStream())) {
                log.error("处理PDF文件出错，图像对象流为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像对象流为空");
            }
            image.setImage(pdfImage.getInputStream());
            // 设置图像高度
            image.setHeight(Objects.isNull(pdfImage.getHeight()) ? image.getImage().getHeight() : pdfImage.getHeight());
            // 设置图像宽度
            image.setWidth(Objects.isNull(pdfImage.getWidth()) ? image.getImage().getWidth() : pdfImage.getWidth());
            // 设置图像缩放比例
            if (Objects.nonNull(pdfImage.getScale())) {
                image.setWidth((int) (image.getWidth() * pdfImage.getScale()));
                image.setHeight((int) (image.getHeight() * pdfImage.getScale()));
            }
            // 设置图像对齐方式
            if (Objects.equals(pdfImage.getHorizontalAlignment(), HorizontalAlignment.CENTER)) {
                float relativeBeginX = page.getWithoutMarginWidth() / 2 - (float) image.getWidth() / 2;
                image.setRelativeBeginX(relativeBeginX);
            } else if (Objects.equals(pdfImage.getHorizontalAlignment(), HorizontalAlignment.RIGHT)) {
                float relativeBeginX = page.getWithoutMarginWidth() - (float) image.getWidth();
                image.setRelativeBeginX(relativeBeginX);
            } else {
                image.setRelativeBeginX(0f);
            }
            // 渲染图像
            image.render();
            // 设置图像下边距
            addComponentInterval(Objects.isNull(pdfImage.getMargin()) ? 10f : pdfImage.getMargin());
            return this;
        }

        /**
         * 保存并输出PDF
         *
         * @param outputStream 输出流
         */
        public void savePdf(OutputStream outputStream) {
            document.appendPage(pageList);
            document.save(outputStream);
            document.close();
        }

    }

    /**
     * 读取PDF数据内部类
     */
    public static class Reader {

    }

    /**
     * PDF页面构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfPage {

        /**
         * 页面纸张大小，默认为A4
         */
        PageSize pageSize;

        /**
         * 页面字体名称，默认为"SimHei"（黑体）
         */
        String pageFontName;

        /**
         * 页面字体大小。默认为12
         */
        Float pageFontSize;

        /**
         * 页面四周边距，默认25
         */
        Float pageMargin;

        /**
         * 页面背景颜色，默认白色
         */
        Color color;

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
        private String fontName;

        /**
         * 文本内容字体大小，默认为12
         */
        private Float fontSize;

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
        private Color fontColor;

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
        private Float characterSpacing;

        /**
         * 文本内容行间距，默认为1
         */
        private Float leading;

        /**
         * 文本内容对齐方式，默认为左对齐
         */
        private HorizontalAlignment horizontalAlignment;

        /**
         * 文本内容之间的间距
         */
        private Float margin;

    }

    /**
     * PDF分割线构造器
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfSplitLine {

        /**
         * 分割线颜色，默认为黑色
         */
        private Color lineColor;

        /**
         * 分割线边距大小，默认为10
         */
        private Float margin;

        /**
         * 分割线是否为点线，默认为false
         */
        private Boolean isDotted = false;

        /**
         * 分割线点线间隔，如果isDotted为false，则dottedSpacing为null
         */
        private Float dottedSpacing;

        /**
         * 分割线点线长度，如果isDotted为false，则dottedLength为null
         */
        private Float dottedLength;

        /**
         * 分割线线长
         */
        private Float lineLength;

        /**
         * 分割线线宽
         */
        private Float lineWidth;

    }

    /**
     * PDF图像构造器
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfImage {

        /**
         * 图像对象流
         */
        private InputStream inputStream;

        /**
         * 图像边距大小，默认为10
         */
        private Float margin = 10f;

        /**
         * 图像对齐方式，默认为左对齐
         */
        private HorizontalAlignment horizontalAlignment;

        /**
         * 图像宽度
         */
        private Integer width;

        /**
         * 图像高度
         */
        private Integer height;

        /**
         * 图像缩放比例，如果图像宽度和高度均为null，则在原图像基础上进行等比例缩放，如果图像宽度和高度不都为null，则在不为空的基础上进行等比例缩放
         */
        private Float scale;

    }

}
