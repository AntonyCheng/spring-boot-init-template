package top.sharehome.springbootinittemplate.utils.document.pdf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.pdf.fop.core.doc.Document;
import org.dromara.pdf.fop.core.doc.component.text.Text;
import org.dromara.pdf.fop.core.doc.page.Page;
import org.dromara.pdf.fop.handler.TemplateHandler;
import org.dromara.pdf.pdfbox.core.base.MemoryPolicy;
import org.dromara.pdf.pdfbox.core.base.PageSize;
import org.dromara.pdf.pdfbox.core.component.Image;
import org.dromara.pdf.pdfbox.core.component.SplitLine;
import org.dromara.pdf.pdfbox.core.component.Textarea;
import org.dromara.pdf.pdfbox.core.enums.FontStyle;
import org.dromara.pdf.pdfbox.core.enums.HorizontalAlignment;
import org.dromara.pdf.pdfbox.handler.PdfHandler;
import org.springframework.core.io.ClassPathResource;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.FontStyleEnum;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.FontWeightEnum;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.HorizontalAlignmentEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * PDF工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class PdfUtils {

    /**
     * POF配置文件路径
     */
    private static final String POF_CONF_PATH;

    /**
     * 设置模板默认字体名称，此处将templates/pdf/XXX.ttf文件的XXX名称提取出来进行填充
     */
    private static final String DEFAULT_FONT_FAMILY = "SourceHanSansCN-Regular";

    // 初始化配置文件和字体，即将配置文件和字体文件处理之后复制到系统临时文件夹中
    static {
        String confResourcePath = "templates" + File.separator + "pdf" + File.separator + "fop.xconf";
        String fontResourcePath = "templates" + File.separator + "pdf" + File.separator + DEFAULT_FONT_FAMILY + ".ttf";
        try (InputStream readConfStream = new ClassPathResource(confResourcePath).getInputStream()) {
            String tempConf = FileUtils.getTempDirectoryPath() + confResourcePath;
            String tempFont = FileUtils.getTempDirectoryPath() + fontResourcePath;
            File existConf = new File(tempConf);
            File existFont = new File(tempFont);
            if (!existConf.exists() || !existFont.exists()) {
                String fontPathname = FileUtils.getTempDirectoryPath() + fontResourcePath;
                File fontFile = new File(fontPathname);
                FileUtils.copyInputStreamToFile(new ClassPathResource(fontResourcePath).getInputStream(), fontFile);
                String replacement = File.separator + fontPathname;
                String confContent = new String(readConfStream.readAllBytes())
                        .replace("ReplaceFontURL", replacement)
                        .replaceAll("ReplaceFontFamily", FilenameUtils.getBaseName(fontResourcePath));
                FileUtils.copyInputStreamToFile(new ByteArrayInputStream(confContent.getBytes()), existConf);
            }
            POF_CONF_PATH = existConf.getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            this.document = TemplateHandler.Document.build().setConfigPath(POF_CONF_PATH);
            this.pageList = new ArrayList<>();
            // 因为是索引，从0开始，所以初始化为-1
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
            if (Objects.isNull(pdfPage)) {
                log.error("处理PDF文件出错，pdfPage参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "pdfPage参数为空");
            }
            // 创建新页面
            Page page = TemplateHandler.Page.build()
                    // 设置页面字体大小，默认为12
                    .setFontSize(String.valueOf(Objects.isNull(pdfPage.getFontSize()) ? 12 : pdfPage.getFontSize()))
                    // 设置页面默认四周边距，默认为25
                    .setMargin(String.valueOf(Objects.isNull(pdfPage.getMargin()) ? 25 : pdfPage.getMargin()))
                    // 设置页面默认背景颜色，默认为白色
                    .setBodyBackgroundColor(Objects.isNull(pdfPage.getColor()) ? "#FFFFFF" : colorToHex(pdfPage.getColor()));
            // 添加到Page列表中
            pageList.add(page);
            // Page页码索引加一
            this.pageIndex++;
            return this;
        }


        /**
         * 添加段落
         *
         * @param textarea 文本内容
         */
        public Writer addParagraph(String textarea) {
            return addParagraph(
                    new PdfParagraph()
                            .setTextContent(textarea)
            );
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param fontSize 字体大小
         */
        public Writer addParagraph(String textarea, Integer fontSize) {
            return addParagraph(
                    new PdfParagraph()
                            .setTextContent(textarea)
                            .setFontSize(fontSize)
            );
        }

        /**
         * 添加段落
         *
         * @param textarea  文本内容
         * @param fontSize  字体大小
         * @param fontColor 字体颜色
         */
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor) {
            return addParagraph(
                    new PdfParagraph()
                            .setTextContent(textarea)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
            );
        }

        /**
         * 添加段落
         *
         * @param textarea   文本内容
         * @param fontSize   字体大小
         * @param fontColor  字体颜色
         * @param fontWeight 字体字重
         * @param fontStyle  字体样式
         */
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor, FontWeightEnum fontWeight, FontStyleEnum fontStyle) {
            return addParagraph(
                    new PdfParagraph()
                            .setTextContent(textarea)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
                            .setFontWeight(fontWeight)
                            .setFontStyle(fontStyle)
            );
        }

        /**
         * 添加段落
         *
         * @param pdfParagraph PDF段落构造器
         */
        public Writer addParagraph(PdfParagraph pdfParagraph) {
            if (Objects.isNull(pdfParagraph)) {
                log.error("处理PDF文件出错，PdfTextarea参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfTextarea参数为空");
            }
            Text text = TemplateHandler.Text.build()
                    // 设置文本
                    .setText(Objects.isNull(pdfParagraph.getTextContent()) ? "" : pdfParagraph.getTextContent())
                    // 设置字体，字体默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setFontFamily(Objects.isNull(pdfParagraph.getFontFamily()) ? DEFAULT_FONT_FAMILY : pdfParagraph.getFontFamily())
                    // 设置字号，默认12
                    .setFontSize(String.valueOf(Objects.isNull(pdfParagraph.getFontSize()) ? 12 : pdfParagraph.getFontSize()))
                    // 设置字重，默认正常
                    .setFontWeight(Objects.isNull(pdfParagraph.getFontWeight()) ? FontWeightEnum.NORMAL.getName() : pdfParagraph.getFontWeight().getName())
                    // 设置斜体，默认正常
                    .setFontStyle(Objects.isNull(pdfParagraph.getFontStyle()) ? FontStyleEnum.NORMAL.getName() : pdfParagraph.getFontStyle().getName())
                    // 设置字体颜色，默认黑色
                    .setFontColor(Objects.isNull(pdfParagraph.getFontColor()) ? "#000000" : colorToHex(pdfParagraph.getFontColor()))
                    // 设置段落首行缩进，默认不缩进
                    .setTextIndent(String.valueOf(Objects.isNull(pdfParagraph.getTextIndent()) ?
                            0 : (Objects.isNull(pdfParagraph.getFontSize()) ? 12 * pdfParagraph.getTextIndent() : pdfParagraph.getFontSize() * pdfParagraph.getTextIndent())))
                    // 设置段间距，默认为5
                    .setSpaceBefore(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) ? 5 : pdfParagraph.getMargin()))
                    .setSpaceAfter(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) ? 5 : pdfParagraph.getMargin()))
                    // 设置行间距，默认为1
                    .setLeading(String.valueOf(Objects.isNull(pdfParagraph.getLeading()) ? 1 : pdfParagraph.getLeading()))
                    // 设置对齐方式，默认两端对齐
                    .setHorizontalStyle(Objects.isNull(pdfParagraph.getHorizontalAlignment()) ? HorizontalAlignmentEnum.JUSTIFY.getName() : pdfParagraph.getHorizontalAlignment().getName());
            // 设置背景颜色，默认没有颜色
            if (Objects.nonNull(pdfParagraph.getBackgroundColor())) {
                text.setBackgroundColor(colorToHex(pdfParagraph.getBackgroundColor()));
            }
            // 设置删除线颜色，默认没有删除线
            if (Objects.nonNull(pdfParagraph.getDeleteLineColor())) {
                text.enableDeleteLine().setDeleteLineColor(colorToHex(pdfParagraph.getDeleteLineColor()));
            }
            // 设置下划线颜色，默认没有下划线
            if (Objects.nonNull(pdfParagraph.getUnderLineColor())) {
                text.enableUnderLine().setUnderLineColor(colorToHex(pdfParagraph.getUnderLineColor()));
            }
            pageList.get(pageIndex).addBodyComponent(text);
            return this;
        }

        /**
         * 添加分割线
         */
//        public Writer addSplitLine() {
//            return addSplitLine(
//                    new PdfSplitLine()
//            );
//        }

        /**
         * 添加分割线
         *
         * @param color     分割线颜色
         * @param lineWidth 分割线宽度
         */
//        public Writer addSplitLine(Color color, Float lineWidth) {
//            return addSplitLine(
//                    new PdfSplitLine()
//                            .setLineColor(color)
//                            .setLineWidth(lineWidth)
//            );
//        }

        /**
         * 添加分割线
         *
         * @param color         分割线颜色
         * @param lineWidth     分割线宽度
         * @param isDotted      分割线是否为点线
         * @param dottedLength  点线长度
         * @param dottedSpacing 点线间隔
         */
//        public Writer addSplitLine(Color color, Float lineWidth, Boolean isDotted, Float dottedLength, Float dottedSpacing) {
//            return addSplitLine(
//                    new PdfSplitLine()
//                            .setLineColor(color)
//                            .setLineWidth(lineWidth)
//                            .setIsDotted(isDotted)
//                            .setDottedLength(dottedLength)
//                            .setDottedSpacing(dottedSpacing)
//            );
//        }

        /**
         * 添加分割线
         *
         * @param pdfSplitLine PDF分割线构造器
         */
//        public Writer addSplitLine(PdfSplitLine pdfSplitLine) {
//            if (Objects.isNull(pdfSplitLine)) {
//                log.error("处理PDF文件出错，PdfSplitLine参数为空");
//                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfSplitLine参数为空");
//            }
//            Page page = pageList.get(pageIndex);
//            SplitLine splitLine = new SplitLine(page);
//            splitLine.setIsWrap(true);
//            // 设置分割线颜色
//            splitLine.setLineColor(Objects.isNull(pdfSplitLine.getLineColor()) ? Color.BLACK : pdfSplitLine.getLineColor());
//            // 设置分割线长度
//            splitLine.setLineLength(Objects.isNull(pdfSplitLine.getLineLength()) ? page.getWidth() - page.getMarginLeft() - page.getMarginRight() : pdfSplitLine.getLineLength());
//            // 设置分割线宽度
//            splitLine.setLineWidth(Objects.isNull(pdfSplitLine.getLineWidth()) ? 1f : pdfSplitLine.getLineWidth());
//            // 判断是否需要点线
//            if (pdfSplitLine.getIsDotted()) {
//                // 设置点线长度
//                splitLine.setDottedLength(Objects.isNull(pdfSplitLine.getLineLength()) ? 1f : pdfSplitLine.getDottedLength());
//                // 设置点线间隔
//                splitLine.setDottedSpacing(Objects.isNull(pdfSplitLine.getDottedSpacing()) ? 0f : pdfSplitLine.getDottedSpacing());
//            }
//            // 渲染分割线
//            splitLine.render();
//            return this;
//        }

        /**
         * 添加图像
         *
         * @param inputStream 图像对象流
         */
//        public Writer addImage(InputStream inputStream) {
//            return addImage(
//                    new PdfImage()
//                            .setInputStream(inputStream)
//            );
//        }

        /**
         * 添加图像
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         */
//        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment) {
//            return addImage(
//                    new PdfImage()
//                            .setInputStream(inputStream)
//                            .setHorizontalAlignment(horizontalAlignment)
//            );
//        }

        /**
         * 天机图象
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         * @param scale               图像缩放比例
         */
//        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment, Float scale) {
//            return addImage(
//                    new PdfImage()
//                            .setInputStream(inputStream)
//                            .setHorizontalAlignment(horizontalAlignment)
//                            .setScale(scale)
//            );
//        }

        /**
         * 添加图像
         *
         * @param inputStream         图像对象流
         * @param horizontalAlignment 图像对齐方式
         * @param width               图像宽度
         * @param height              图像高度
         */
//        public Writer addImage(InputStream inputStream, HorizontalAlignment horizontalAlignment, Integer width, Integer height) {
//            return addImage(
//                    new PdfImage()
//                            .setInputStream(inputStream)
//                            .setHorizontalAlignment(horizontalAlignment)
//                            .setWidth(width)
//                            .setHeight(height)
//            );
//        }

        /**
         * 添加图像
         *
         * @param pdfImage PDF图像构造器
         */
//        public Writer addImage(PdfImage pdfImage) {
//            if (Objects.isNull(pdfImage)) {
//                log.error("处理PDF文件出错，PdfImage参数为空");
//                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数为空");
//            }
//            Page page = pageList.get(pageIndex);
//            Image image = new Image(page);
//            image.setIsWrap(true);
//            // 设置图像对象流
//            if (Objects.isNull(pdfImage.getInputStream())) {
//                log.error("处理PDF文件出错，图像对象流为空");
//                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像对象流为空");
//            }
//            image.setImage(pdfImage.getInputStream());
//            // 设置图像高度
//            image.setHeight(Objects.isNull(pdfImage.getHeight()) ? image.getImage().getHeight() : pdfImage.getHeight());
//            // 设置图像宽度
//            image.setWidth(Objects.isNull(pdfImage.getWidth()) ? image.getImage().getWidth() : pdfImage.getWidth());
//            // 设置图像缩放比例
//            if (Objects.nonNull(pdfImage.getScale())) {
//                image.setWidth((int) (image.getWidth() * pdfImage.getScale()));
//                image.setHeight((int) (image.getHeight() * pdfImage.getScale()));
//            }
//            // 设置图像对齐方式
//            if (Objects.equals(pdfImage.getHorizontalAlignment(), HorizontalAlignment.CENTER)) {
//                float relativeBeginX = page.getWithoutMarginWidth() / 2 - (float) image.getWidth() / 2;
//                image.setRelativeBeginX(relativeBeginX);
//            } else if (Objects.equals(pdfImage.getHorizontalAlignment(), HorizontalAlignment.RIGHT)) {
//                float relativeBeginX = page.getWithoutMarginWidth() - (float) image.getWidth();
//                image.setRelativeBeginX(relativeBeginX);
//            } else {
//                image.setRelativeBeginX(0f);
//            }
//            // 渲染图像
//            image.render();
//            return this;
//        }

        /**
         * 将Word写入响应流
         *
         * @param fileName 响应文件名
         * @param response 响应流
         */
        public void doWrite(String fileName, HttpServletResponse response) {
            try {
                handlePdfResponse(fileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                doWrite(outputStream);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.PDF_FILE_ERROR);
            }
        }

        /**
         * 保存并输出PDF
         *
         * @param outputStream 输出流
         */
        public void doWrite(OutputStream outputStream) {
            document.addPage(pageList.toArray(new Page[0]));
            document.transform(outputStream);
        }

        /**
         * 处理ContentType是PDF格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private static void handlePdfResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".docx";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".docx";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/pdf;charset=UTF-8");
        }

        /**
         * 将Color转换成16进制数
         */
        private static String colorToHex(Color color) {
            if (Objects.isNull(color)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "Color转换为16进制数时，Color对象不能为空");
            }
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
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
         * 页面字体大小。默认为12
         */
        Integer fontSize;

        /**
         * 页面四周边距，默认25
         */
        Integer margin;

        /**
         * 页面背景颜色，默认白色
         */
        Color color;

    }

    /**
     * PDF段落构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfParagraph {

        /**
         * 段落文本内容
         */
        private String textContent;

        /**
         * 段落字体
         */
        private String fontFamily;

        /**
         * 段落字体大小
         */
        private Integer fontSize;

        /**
         * 段落字重
         */
        private FontWeightEnum fontWeight;

        /**
         * 段落样式
         */
        private FontStyleEnum fontStyle;

        /**
         * 段落字体颜色
         */
        private Color fontColor;

        /**
         * 段落首行缩进字符数
         */
        private Integer textIndent;

        /**
         * 段落背景颜色
         */
        private Color backgroundColor;

        /**
         * 段落删除线颜色
         */
        private Color deleteLineColor;

        /**
         * 段落下划线颜色
         */
        private Color underLineColor;

        /**
         * 段落行间距
         */
        private Integer leading;

        /**
         * 段落段间距
         */
        private Integer margin;

        /**
         * 段落对齐方式
         */
        private HorizontalAlignmentEnum horizontalAlignment;

    }

    /**
     * PDF分割线构造类
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
     * PDF图像构造类
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
