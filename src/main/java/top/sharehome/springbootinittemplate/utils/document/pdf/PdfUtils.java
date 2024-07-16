package top.sharehome.springbootinittemplate.utils.document.pdf;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.pdf.fop.core.doc.Document;
import org.dromara.pdf.fop.core.doc.component.image.Image;
import org.dromara.pdf.fop.core.doc.component.line.SplitLine;
import org.dromara.pdf.fop.core.doc.component.text.Text;
import org.dromara.pdf.fop.core.doc.page.Page;
import org.dromara.pdf.fop.handler.TemplateHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.*;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
            String fontPathname = FileUtils.getTempDirectoryPath() + fontResourcePath;
            File fontFile = new File(fontPathname);
            String replacement = "/" + fontPathname;
            String confContent = new String(readConfStream.readAllBytes())
                    .replace("ReplaceFontURL", replacement)
                    .replaceAll("ReplaceFontFamily", FilenameUtils.getBaseName(fontResourcePath));
            if (!existConf.exists() || !existFont.exists() || existConf.length() != confContent.getBytes().length) {
                FileUtils.copyInputStreamToFile(new ClassPathResource(fontResourcePath).getInputStream(), fontFile);
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
                    .setFontSize(String.valueOf(Objects.isNull(pdfPage.getFontSize()) || pdfPage.getFontSize() <= 0 ? 12 : pdfPage.getFontSize()))
                    // 设置页面默认四周边距，默认为25
                    .setMargin(String.valueOf(Objects.isNull(pdfPage.getMargin()) || pdfPage.getMargin() <= 0 ? 25 : pdfPage.getMargin()))
                    // 设置页面默认背景颜色，默认为白色
                    .setBodyBackgroundColor(colorToHex(Objects.isNull(pdfPage.getColor()) ? Color.WHITE : pdfPage.getColor()));
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
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor, FontWeight fontWeight, FontStyle fontStyle) {
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
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfParagraph)) {
                log.error("处理PDF文件出错，PdfParagraph参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfParagraph参数为空");
            }
            Text text = TemplateHandler.Text.build()
                    // 设置文本
                    .setText(Objects.isNull(pdfParagraph.getTextContent()) ? "" : pdfParagraph.getTextContent())
                    // 设置字体，字体默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setFontFamily(Objects.isNull(pdfParagraph.getFontFamily()) ? DEFAULT_FONT_FAMILY : pdfParagraph.getFontFamily())
                    // 设置字号，默认12
                    .setFontSize(String.valueOf(Objects.isNull(pdfParagraph.getFontSize()) || pdfParagraph.getFontSize() <= 0 ? 12 : pdfParagraph.getFontSize()))
                    // 设置字重，默认正常
                    .setFontWeight(Objects.isNull(pdfParagraph.getFontWeight()) ? FontWeight.NORMAL.getName() : pdfParagraph.getFontWeight().getName())
                    // 设置斜体，默认正常
                    .setFontStyle(Objects.isNull(pdfParagraph.getFontStyle()) ? FontStyle.NORMAL.getName() : pdfParagraph.getFontStyle().getName())
                    // 设置字体颜色，默认黑色
                    .setFontColor(colorToHex(Objects.isNull(pdfParagraph.getFontColor()) ? Color.BLACK : pdfParagraph.getFontColor()))
                    // 设置段落首行缩进，默认不缩进
                    .setTextIndent(String.valueOf(Objects.isNull(pdfParagraph.getTextIndent()) || pdfParagraph.getTextIndent() <= 0 ?
                            0 : (Objects.isNull(pdfParagraph.getFontSize()) ? 12 * pdfParagraph.getTextIndent() : pdfParagraph.getFontSize() * pdfParagraph.getTextIndent())))
                    // 设置段间距，默认为5
                    .setSpaceBefore(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) || pdfParagraph.getMargin() <= 0 ? 5 : pdfParagraph.getMargin()))
                    .setSpaceAfter(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) || pdfParagraph.getMargin() <= 0 ? 5 : pdfParagraph.getMargin()))
                    // 设置行间距，默认为1
                    .setLeading(String.valueOf(Objects.isNull(pdfParagraph.getLeading()) || pdfParagraph.getLeading() <= 0 ? 1 : pdfParagraph.getLeading()))
                    // 设置对齐方式，默认两端对齐
                    .setHorizontalStyle(Objects.isNull(pdfParagraph.getParagraphHorizontal()) ? ParagraphHorizontal.JUSTIFY.getName() : pdfParagraph.getParagraphHorizontal().getName());
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
        public Writer addSplitLine() {
            return addSplitLine(
                    new PdfSplitLine()
            );
        }

        /**
         * 添加分割线
         *
         * @param style 分割线样式
         */
        public Writer addSplitLine(SplitLineStyle style) {
            return addSplitLine(
                    new PdfSplitLine().setStyle(style)
            );
        }

        /**
         * 添加分割线
         *
         * @param style 分割线样式
         * @param color 分割线颜色
         */
        public Writer addSplitLine(SplitLineStyle style, Color color) {
            return addSplitLine(
                    new PdfSplitLine()
                            .setStyle(style)
                            .setColor(color)
            );
        }

        /**
         * 添加分割线
         *
         * @param style               分割线样式
         * @param color               分割线颜色
         * @param length              分割线长度占比，合理范围[0,100]，允许范围[0,无穷大)
         * @param margin              分割线上下边距
         * @param splitLineHorizontal 分割线对齐方式
         */
        public Writer addSplitLine(SplitLineStyle style, Color color, Integer length, Integer margin, SplitLineHorizontal splitLineHorizontal) {
            return addSplitLine(
                    new PdfSplitLine()
                            .setStyle(style)
                            .setColor(color)
                            .setLength(length)
                            .setMargin(margin)
                            .setSplitLineHorizontal(splitLineHorizontal)
            );
        }

        /**
         * 添加分割线
         *
         * @param pdfSplitLine PDF分割线构造器
         */
        public Writer addSplitLine(PdfSplitLine pdfSplitLine) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfSplitLine)) {
                log.error("处理PDF文件出错，PdfSplitLine参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfSplitLine参数为空");
            }
            SplitLine splitLine = TemplateHandler.SplitLine.build()
                    // 设置分割线长度占比，默认100，合理范围[0,100]，允许范围[0,无穷大)
                    .setLength(Objects.isNull(pdfSplitLine.getLength()) || pdfSplitLine.getLength() <= 0 ? "100%" : pdfSplitLine.getLength() + "%")
                    // 设置分割线样式，默认实线
                    .setStyle(Objects.isNull(pdfSplitLine.getStyle()) ? SplitLineStyle.SOLID.getName() : pdfSplitLine.getStyle().getName())
                    // 设置分割线颜色，默认黑色
                    .setColor(colorToHex(Objects.isNull(pdfSplitLine.getColor()) ? Color.BLACK : pdfSplitLine.getColor()))
                    // 设置分割线上下边距，默认1
                    .setMarginTop(String.valueOf(Objects.isNull(pdfSplitLine.getMargin()) || pdfSplitLine.getMargin() <= 0 ? 1 : pdfSplitLine.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfSplitLine.getMargin()) || pdfSplitLine.getMargin() <= 0 ? 1 : pdfSplitLine.getMargin()))
                    // 设置分割线对齐方式，默认居中
                    .setHorizontalStyle(Objects.isNull(pdfSplitLine.getStyle()) ? SplitLineHorizontal.CENTER.getName() : pdfSplitLine.getStyle().getName());
            pageList.get(pageIndex).addBodyComponent(splitLine);
            return this;
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         */
        public Writer addImage(MultipartFile multipartFile) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                String extension = FilenameUtils.getExtension(StringUtils.isNotBlank(multipartFile.getOriginalFilename()) ? multipartFile.getOriginalFilename() : multipartFile.getName());
                ImageExtension imageExtension = ImageExtension.getEnumByName("." + extension);
                return addImage(inputStream, imageExtension);
            } catch (IOException e) {
                log.error("处理PDF文件出错，获取图像数据流失败");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream    图像数据流
         * @param imageExtension 图像拓展名
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension) {
            String imageTempPath = copyImageToTempDir(inputStream, imageExtension);
            return addImage(imageTempPath);
        }

        /**
         * 添加图像
         *
         * @param path 图像所在路径
         */
        public Writer addImage(String path) {
            return addImage(
                    new PdfImage()
                            .setPath(path)
            );
        }

        /**
         * 添加图像
         *
         * @param multipartFile   图像文件
         * @param imageHorizontal 图像对齐方式
         */
        public Writer addImage(MultipartFile multipartFile, ImageHorizontal imageHorizontal) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                String extension = FilenameUtils.getExtension(StringUtils.isNotBlank(multipartFile.getOriginalFilename()) ? multipartFile.getOriginalFilename() : multipartFile.getName());
                ImageExtension imageExtension = ImageExtension.getEnumByName("." + extension);
                return addImage(inputStream, imageExtension, imageHorizontal);
            } catch (IOException e) {
                log.error("处理PDF文件出错，获取图像数据流失败");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream     图像数据流
         * @param imageExtension  图像拓展名
         * @param imageHorizontal 图像对齐方式
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, ImageHorizontal imageHorizontal) {
            String imageTempPath = copyImageToTempDir(inputStream, imageExtension);
            return addImage(imageTempPath, imageHorizontal);
        }

        /**
         * 添加图像
         *
         * @param path            图像所在路径
         * @param imageHorizontal 图像对齐方式
         */
        public Writer addImage(String path, ImageHorizontal imageHorizontal) {
            return addImage(
                    new PdfImage()
                            .setPath(path)
                            .setImageHorizontal(imageHorizontal)
            );
        }

        /**
         * 添加图像
         *
         * @param multipartFile   图像文件
         * @param imageHorizontal 图像对齐方式
         * @param width           图像宽度
         * @param height          图像高度
         */
        public Writer addImage(MultipartFile multipartFile, ImageHorizontal imageHorizontal, Integer width, Integer height) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                String extension = FilenameUtils.getExtension(StringUtils.isNotBlank(multipartFile.getOriginalFilename()) ? multipartFile.getOriginalFilename() : multipartFile.getName());
                ImageExtension imageExtension = ImageExtension.getEnumByName("." + extension);
                return addImage(inputStream, imageExtension, imageHorizontal, width, height);
            } catch (IOException e) {
                log.error("处理PDF文件出错，获取图像数据流失败");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream     图像数据流
         * @param imageExtension  图像拓展名
         * @param imageHorizontal 图像对齐方式
         * @param width           图像宽度
         * @param height          图像高度
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, ImageHorizontal imageHorizontal, Integer width, Integer height) {
            String imageTempPath = copyImageToTempDir(inputStream, imageExtension);
            return addImage(imageTempPath, imageHorizontal, width, height);
        }

        /**
         * 天机图象
         *
         * @param path            图像所在路径
         * @param imageHorizontal 图像对齐方式
         * @param width           图像宽度
         * @param height          图像高度
         */
        public Writer addImage(String path, ImageHorizontal imageHorizontal, Integer width, Integer height) {
            return addImage(
                    new PdfImage()
                            .setPath(path)
                            .setImageHorizontal(imageHorizontal)
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
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfImage)) {
                log.error("处理PDF文件出错，PdfImage参数为空");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数为空");
            }
            if (!Files.isRegularFile(Path.of(pdfImage.getPath()))) {
                log.error("处理PDF文件出错，PdfImage参数中图片所在路径[path]为不存在或者非文件");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数中图片所在路径为不存在或者非文件");
            }
            Image image = TemplateHandler.Image.build()
                    // 设置图像所在路径
                    .setPath("/" + pdfImage.getPath())
                    // 设置图像对齐方式，默认左对齐
                    .setHorizontalStyle(Objects.isNull(pdfImage.getImageHorizontal()) ? ImageHorizontal.LEFT.getName() : pdfImage.getImageHorizontal().getName())
                    // 设置图像宽度
                    .setWidth(Objects.isNull(pdfImage.getWidth()) || pdfImage.getWidth() <= 0 ? null : String.valueOf(pdfImage.getWidth()))
                    // 设置图像长度
                    .setHeight(Objects.isNull(pdfImage.getHeight()) || pdfImage.getHeight() <= 0 ? null : String.valueOf(pdfImage.getHeight()))
                    // 设置图像上下边距，默认2
                    .setMarginTop(String.valueOf(Objects.isNull(pdfImage.getMargin()) || pdfImage.getMargin() <= 0 ? 2 : pdfImage.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfImage.getMargin()) || pdfImage.getMargin() <= 0 ? 2 : pdfImage.getMargin()));
            pageList.get(pageIndex).addBodyComponent(image);
            return this;
        }

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

        /**
         * 设置PDF插入图片存放在系统临时文件夹中的子路径
         */
        private static final String TEMP_IMAGE_DIR = FileUtils.getTempDirectoryPath() + "templates" + File.separator + "pdf" + File.separator + "tempImage" + File.separator;

        /**
         * 将图像拷贝至临时文件夹中
         *
         * @param inputStream    图像数据流
         * @param imageExtension 图像拓展名
         */
        private static String copyImageToTempDir(InputStream inputStream, ImageExtension imageExtension) {
            try {
                if (Objects.isNull(imageExtension)) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像拓展名为空");
                }
                String imageTempPath = TEMP_IMAGE_DIR + "pdfImage_" + UUID.randomUUID().toString().replace("-", "") + imageExtension.getName();
                Path path = Path.of(imageTempPath);
                Files.deleteIfExists(path);
                FileUtils.copyInputStreamToFile(inputStream, new File(imageTempPath));
                return imageTempPath;
            } catch (IOException e) {
                log.error("处理PDF文件出错，图像数据拷贝异常");
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像数据拷贝异常");
            }
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
         * 页面字体大小
         */
        Integer fontSize;

        /**
         * 页面四周边距
         */
        Integer margin;

        /**
         * 页面背景颜色
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
        private FontWeight fontWeight;

        /**
         * 段落样式
         */
        private FontStyle fontStyle;

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
        private ParagraphHorizontal paragraphHorizontal;

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
         * 分割线样式
         */
        private SplitLineStyle style;

        /**
         * 分割线颜色
         */
        private Color color;

        /**
         * 分割线长度占比，合理范围[0,100]，允许范围[0,无穷大)
         */
        private Integer length;

        /**
         * 分割线上下边距
         */
        private Integer margin;

        /**
         * 分割线对齐方式
         */
        private SplitLineHorizontal splitLineHorizontal;

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
         * 图像所在路径
         */
        private String path;

        /**
         * 图像宽度
         */
        private Integer width;

        /**
         * 图像高度
         */
        private Integer height;

        /**
         * 图像对齐方式
         */
        private ImageHorizontal imageHorizontal;

        /**
         * 图像上下边距
         */
        private Integer margin;

    }

}
