package top.sharehome.springbootinittemplate.utils.document.pdf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.pdf.fop.core.doc.Document;
import org.dromara.pdf.fop.core.doc.component.barcode.Barcode;
import org.dromara.pdf.fop.core.doc.component.image.Image;
import org.dromara.pdf.fop.core.doc.component.line.SplitLine;
import org.dromara.pdf.fop.core.doc.component.table.*;
import org.dromara.pdf.fop.core.doc.component.text.Text;
import org.dromara.pdf.fop.core.doc.page.Page;
import org.dromara.pdf.fop.core.doc.watermark.Watermark;
import org.dromara.pdf.fop.handler.TemplateHandler;
import org.dromara.pdf.pdfbox.core.ext.analyzer.DocumentAnalyzer;
import org.dromara.pdf.pdfbox.core.info.ImageInfo;
import org.dromara.pdf.pdfbox.core.info.TextInfo;
import org.dromara.pdf.pdfbox.handler.PdfHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

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

    /**
     * 设置PDF插入图像存放在系统临时文件夹中的子路径
     */
    private static final String TEMP_IMAGE_DIR = (StringUtils.endsWith(FileUtils.getTempDirectoryPath(), File.separator) ? FileUtils.getTempDirectoryPath() : FileUtils.getTempDirectoryPath() + File.separator) + "templates" + File.separator + "pdf" + File.separator + "tempImage";

    /**
     * 设置PDF导出模板文件存放在系统临时文件夹中的子路径
     */
    private static final String TEMP_TEMPLATE_DIR = (StringUtils.endsWith(FileUtils.getTempDirectoryPath(), File.separator) ? FileUtils.getTempDirectoryPath() : FileUtils.getTempDirectoryPath() + File.separator) + "templates" + File.separator + "pdf" + File.separator + "tempTemplate";

    // 初始化配置文件和字体，即将配置文件和字体文件处理之后复制到系统临时文件夹中
    static {
        String confResourcePath = "templates" + File.separator + "pdf" + File.separator + "fop.xconf";
        String fontResourcePath = "templates" + File.separator + "pdf" + File.separator + DEFAULT_FONT_FAMILY + ".ttf";
        try (InputStream readConfStream = new ClassPathResource(confResourcePath).getInputStream()) {
            String tempDirectoryPath = StringUtils.endsWith(FileUtils.getTempDirectoryPath(), File.separator) ? FileUtils.getTempDirectoryPath() : FileUtils.getTempDirectoryPath() + File.separator;
            String tempConf = tempDirectoryPath + confResourcePath;
            String tempFont = tempDirectoryPath + fontResourcePath;
            File existConf = new File(tempConf);
            File existFont = new File(tempFont);
            String fontPathname = tempDirectoryPath + fontResourcePath;
            File fontFile = new File(fontPathname);
            String replacement = StringUtils.startsWith(fontPathname, "/") ? fontPathname : "/" + fontPathname;
            String confContent = new String(readConfStream.readAllBytes())
                    .replace("ReplaceFontURL", replacement)
                    .replaceAll("ReplaceFontFamily", FilenameUtils.getBaseName(fontResourcePath));
            if (!existConf.exists() || !existFont.exists() || existConf.length() != confContent.getBytes().length) {
                FileUtils.copyInputStreamToFile(new ClassPathResource(fontResourcePath).getInputStream(), fontFile);
                FileUtils.copyInputStreamToFile(new ByteArrayInputStream(confContent.getBytes()), existConf);
            }
            POF_CONF_PATH = existConf.getPath();
        } catch (IOException e) {
            throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "初始化配置文件失败");
        }
    }

    /**
     * 输出PDF模板内部类
     */
    public static class Template {

        /**
         * 导出PDF模板目录下的模板文件到响应流，模板目录一定是resources文件夹下templates/pdf目录
         *
         * @param templateName 模板名称（需要带上扩展名）
         * @param exportDataSource 导出数据源类型
         * @param tagMap       标签Map
         * @param filename     导出文件名称
         * @param response     响应流
         */
        public void export(String templateName, ExportDataSource exportDataSource, Map<String, Object> tagMap, String filename, HttpServletResponse response) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                handlePdfResponse(filename, response);
                export(templateName, exportDataSource, tagMap, outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 导出PDF模板目录下的模板文件到输出流，模板目录一定是resources文件夹下templates/pdf目录
         *
         * @param templateName 模板名称（需要带上扩展名），fo文件对应FREEMARKER数据源和THYMELEAF数据源，jte文件对应JTE数据源
         * @param exportDataSource 导出数据源类型
         * @param tagMap 标签Map
         * @param outputStream 输出流
         */
        public void export(String templateName, ExportDataSource exportDataSource, Map<String, Object> tagMap, OutputStream outputStream) {
            try {
                if (Objects.isNull(outputStream)) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板文件输出时，输出流为空");
                }
                if (Objects.isNull(tagMap)) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板文件输出时，标签Map为空");
                }
                // 检查模板文件是否存在
                ClassPathResource classPathResource = new ClassPathResource("templates/pdf/" + templateName);
                if (StringUtils.isBlank(templateName) || !classPathResource.exists()) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板文件[" + templateName + "]未找到");
                }
                // 根据数据源和模板文件扩展名进行匹配判断
                String extension = FilenameUtils.getExtension(templateName);
                if (Objects.equals(exportDataSource, ExportDataSource.FREEMARKER) && Objects.equals(extension, "fo")) {
                    InputStream inputStream = classPathResource.getInputStream();
                    // 将模板文件拷贝至系统临时文件夹中
                    String templatePath = copyTemplateToTempDir(inputStream, ExportExtension.FO);
                    // 进行模板导出操作
                    TemplateHandler.DataSource.Freemarker.setTemplatePath(TEMP_TEMPLATE_DIR);
                    TemplateHandler.Template.build().setConfigPath(POF_CONF_PATH).setDataSource(
                            TemplateHandler.DataSource.Freemarker.build().setTemplateName(FilenameUtils.getName(templatePath)).setTemplateData(tagMap)
                    ).transform(outputStream);
                } else if (Objects.equals(exportDataSource, ExportDataSource.THYMELEAF) && Objects.equals(extension, "fo")) {
                    InputStream inputStream = classPathResource.getInputStream();
                    // 将模板文件拷贝至系统临时文件夹中
                    String templatePath = copyTemplateToTempDir(inputStream, ExportExtension.FO);
                    // 进行模板导出操作
                    TemplateHandler.Template.build().setConfigPath(POF_CONF_PATH).setDataSource(
                            TemplateHandler.DataSource.Thymeleaf.build().setTemplatePath(templatePath).setTemplateData(tagMap)
                    ).transform(outputStream);
                } else if (Objects.equals(exportDataSource, ExportDataSource.JTE) && Objects.equals(extension, "jte")) {
                    InputStream inputStream = classPathResource.getInputStream();
                    // 将模板文件拷贝至系统临时文件夹中
                    String templatePath = copyTemplateToTempDir(inputStream, ExportExtension.JTE);
                    // 进行模板导出操作
                    TemplateHandler.Template.build().setConfigPath(POF_CONF_PATH).setDataSource(
                            TemplateHandler.DataSource.Jte.build().setTemplatePath(templatePath).setTemplateData(tagMap)
                    ).transform(outputStream);
                } else {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "指定模板文件扩展名和数据源不匹配");
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板导出时，数据流处理错误");
            }
        }

        /**
         * 处理ContentType是PDF格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handlePdfResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".pdf";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".pdf";
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
         * 将模板文件拷贝至临时文件夹中
         *
         * @param inputStream    模板文件按数据流
         * @param exportExtension 模板文件扩展名
         */
        private String copyTemplateToTempDir(InputStream inputStream, ExportExtension exportExtension) {
            try {
                if (Objects.isNull(exportExtension)) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板文件扩展名为空");
                }
                String templateTempPath = TEMP_TEMPLATE_DIR + File.separator + "pdfTemplate_" + UUID.randomUUID().toString().replace("-", "") + exportExtension.getName();
                Path path = Path.of(templateTempPath);
                Files.deleteIfExists(path);
                FileUtils.copyInputStreamToFile(inputStream, new File(templateTempPath));
                return templateTempPath;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "模板文件数据拷贝异常");
            }
        }

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
        private final List<Page> pageList;

        /**
         * 当前页面索引
         */
        private int pageIndex;

        /**
         * 写入PDF数据内部类构造器
         */
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
         * @param pdfPage PDF页面构造类
         */
        public Writer addPage(PdfPage pdfPage) {
            if (Objects.isNull(pdfPage)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "pdfPage参数为空");
            }
            // 创建新页面
            Page page = TemplateHandler.Page.build()
                    // 设置页面字号，默认为12
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
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param texts 水印文本数组，数组元素个数对应文本行数
         */
        public Writer addWatermark(String... texts) {
            return addWatermark(null, Arrays.stream(texts).toList(), null, null, null, null, null, null);
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param texts 水印文本列表，列表元素个数对应文本行数
         */
        public Writer addWatermark(List<String> texts) {
            return addWatermark(null, texts, null, null, null, null, null, null);
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param id 水印ID
         * @param texts 水印文本列表，列表元素个数对应文本行数
         */
        public Writer addWatermark(String id, List<String> texts) {
            return addWatermark(id, texts, null, null, null, null, null, null);
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param id 水印ID
         * @param texts 水印文本列表，列表元素个数对应文本行数
         * @param fontFamily 水印文本字体
         * @param fontSize 水印文本字号
         */
        public Writer addWatermark(String id, List<String> texts, String fontFamily, Integer fontSize) {
            return addWatermark(id, texts, fontFamily, fontSize, null, null, null, null);
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param id 水印ID
         * @param texts 水印文本列表，列表元素个数对应文本行数
         * @param fontFamily 水印文本字体
         * @param fontSize 水印文本字号
         * @param fontAlpha 水印文本透明度
         */
        public Writer addWatermark(String id, List<String> texts, String fontFamily, Integer fontSize, Integer fontAlpha) {
            return addWatermark(id, texts, fontFamily, fontSize, fontAlpha, null, null, null);
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param id 水印ID
         * @param texts 水印文本列表，列表元素个数对应文本行数
         * @param fontFamily 水印文本字体
         * @param fontSize 水印文本字号
         * @param fontAlpha 水印文本透明度
         * @param width 水印文本宽度
         * @param height 水印文本高度
         * @param showWidth 水印文本显示宽度
         */
        public Writer addWatermark(String id, List<String> texts, String fontFamily, Integer fontSize, Integer fontAlpha, Integer width, Integer height, Integer showWidth) {
            return addWatermark(
                    new PdfWatermark()
                            .setId(id)
                            .setTexts(texts)
                            .setFontFamily(fontFamily)
                            .setFontSize(fontSize)
                            .setFontAlpha(fontAlpha)
                            .setWidth(width)
                            .setHeight(height)
                            .setShowWidth(showWidth)
            );
        }

        /**
         * 添加水印（若在Docker中使用，水印暂不支持中文）
         *
         * @param pdfWatermark PDF水印构造类
         */
        public Writer addWatermark(PdfWatermark pdfWatermark) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfWatermark)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfWatermark参数为空");
            }
            Watermark watermark = TemplateHandler.Watermark.build()
                    // 设置水印ID，必须保持唯一性，默认UUID
                    .setId(StringUtils.isBlank(pdfWatermark.getId()) ? UUID.randomUUID().toString().replace("-", "") : pdfWatermark.getId())
                    // 设置水印文本列表
                    .setText(CollectionUtils.isEmpty(pdfWatermark.getTexts()) ? null : pdfWatermark.getTexts())
                    // 设置水印文本字体，字体默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setFontFamily(StringUtils.isBlank(pdfWatermark.getFontFamily()) ? DEFAULT_FONT_FAMILY : pdfWatermark.getFontFamily())
                    // 设置水印文本字号，默认25
                    .setFontSize((Objects.isNull(pdfWatermark.getFontSize()) || pdfWatermark.getFontSize() <= 0 ? 25 : pdfWatermark.getFontSize()) + "px")
                    // 设置水印文本透明度，范围是0-255，值越小越透明，默认50
                    .setFontAlpha(String.valueOf(Objects.isNull(pdfWatermark.getFontAlpha()) || pdfWatermark.getFontAlpha() < 0 || pdfWatermark.getFontAlpha() > 255 ? 50 : pdfWatermark.getFontAlpha()))
                    // 设置水印文本宽度，默认200px
                    .setWidth((Objects.isNull(pdfWatermark.getWidth()) || pdfWatermark.getWidth() <= 0 ? 200 : pdfWatermark.getWidth()) + "px")
                    // 设置水印文本高度，默认200px
                    .setHeight((Objects.isNull(pdfWatermark.getHeight()) || pdfWatermark.getHeight() <= 0 ? 200 : pdfWatermark.getHeight()) + "px")
                    // 设置水印文本显示宽度，默认180px
                    .setShowWidth((Objects.isNull(pdfWatermark.getShowWidth()) || pdfWatermark.getShowWidth() <= 0 ? 180 : pdfWatermark.getShowWidth()) + "px")
                    // 设置水印文本旋转45°
                    .setRadians("45")
                    // 设置水印文本粗体
                    .setFontStyle("bold")
                    // 设置水印文本图像临时文件夹
                    .setTempDir(TEMP_IMAGE_DIR)
                    // 设置覆盖原有水印
                    .enableOverwrite();
            pageList.get(pageIndex).setBodyWatermark(watermark);
            return this;
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         */
        public Writer addParagraph(String textarea) {
            return addParagraph(textarea, null, null, null, null);
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param fontSize 字号
         */
        public Writer addParagraph(String textarea, Integer fontSize) {
            return addParagraph(textarea, fontSize, null, null, null);
        }

        /**
         * 添加段落
         *
         * @param textarea  文本内容
         * @param fontSize  字号
         * @param fontColor 字体颜色
         */
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor) {
            return addParagraph(textarea, fontSize, fontColor, null, null);
        }

        /**
         * 添加段落
         *
         * @param textarea   文本内容
         * @param fontSize   字号
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
         * @param pdfParagraph PDF段落构造类
         */
        public Writer addParagraph(PdfParagraph pdfParagraph) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfParagraph)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfParagraph参数为空");
            }
            Text text = TemplateHandler.Text.build()
                    // 设置段落文本
                    .setText(Objects.isNull(pdfParagraph.getTextContent()) ? "" : pdfParagraph.getTextContent())
                    // 设置段落字体，字体默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setFontFamily(Objects.isNull(pdfParagraph.getFontFamily()) ? DEFAULT_FONT_FAMILY : pdfParagraph.getFontFamily())
                    // 设置段落字号，默认12
                    .setFontSize(String.valueOf(Objects.isNull(pdfParagraph.getFontSize()) || pdfParagraph.getFontSize() <= 0 ? 12 : pdfParagraph.getFontSize()))
                    // 设置段落字重，默认正常
                    .setFontWeight(Objects.isNull(pdfParagraph.getFontWeight()) ? FontWeight.NORMAL.getName() : pdfParagraph.getFontWeight().getName())
                    // 设置段落斜体，默认正常
                    .setFontStyle(Objects.isNull(pdfParagraph.getFontStyle()) ? FontStyle.NORMAL.getName() : pdfParagraph.getFontStyle().getName())
                    // 设置段落字体颜色，默认黑色
                    .setFontColor(colorToHex(Objects.isNull(pdfParagraph.getFontColor()) ? Color.BLACK : pdfParagraph.getFontColor()))
                    // 设置段落首行缩进，默认不缩进
                    .setTextIndent(String.valueOf(Objects.isNull(pdfParagraph.getTextIndent()) || pdfParagraph.getTextIndent() <= 0 ?
                            0 : (Objects.isNull(pdfParagraph.getFontSize()) ? 12 * pdfParagraph.getTextIndent() : pdfParagraph.getFontSize() * pdfParagraph.getTextIndent())))
                    // 设置段落上下边距，默认为1
                    .setMarginTop(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) || pdfParagraph.getMargin() <= 0 ? 1 : pdfParagraph.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfParagraph.getMargin()) || pdfParagraph.getMargin() <= 0 ? 1 : pdfParagraph.getMargin()))
                    // 设置段落行间距，默认为1
                    .setLeading(String.valueOf(Objects.isNull(pdfParagraph.getLeading()) || pdfParagraph.getLeading() <= 0 ? 1 : pdfParagraph.getLeading()))
                    // 设置段落对齐方式，默认两端对齐
                    .setHorizontalStyle(Objects.isNull(pdfParagraph.getParagraphHorizontal()) ? ParagraphHorizontal.JUSTIFY.getName() : pdfParagraph.getParagraphHorizontal().getName());
            // 设置段落背景颜色，默认没有颜色
            if (Objects.nonNull(pdfParagraph.getBackgroundColor())) {
                text.setBackgroundColor(colorToHex(pdfParagraph.getBackgroundColor()));
            }
            // 设置段落删除线颜色，默认没有删除线
            if (Objects.nonNull(pdfParagraph.getDeleteLineColor())) {
                text.enableDeleteLine().setDeleteLineColor(colorToHex(pdfParagraph.getDeleteLineColor()));
            }
            // 设置段落下划线颜色，默认没有下划线
            if (Objects.nonNull(pdfParagraph.getUnderLineColor())) {
                text.enableUnderLine().setUnderLineColor(colorToHex(pdfParagraph.getUnderLineColor()));
            }
            pageList.get(pageIndex).addBodyComponent(text);
            return this;
        }

        /**
         * 添加表格
         *
         * @param pdfTableDataArray 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         */
        public Writer addTable(String[][] pdfTableDataArray) {
            return addTable(pdfTableDataArray, false, false, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableDataArray 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param fontFamily  表格文本内容字体
         */
        public Writer addTable(String[][] pdfTableDataArray, String fontFamily) {
            return addTable(pdfTableDataArray, false, false, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableDataArray 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param existHeader  是否存在表头，如果存在则自动提取数据中第一行作为表头
         * @param existFooter  是否存在表尾，如果存在则自动提取数据中最后一行作为表尾
         */
        public Writer addTable(String[][] pdfTableDataArray, boolean existHeader, boolean existFooter) {
            return addTable(pdfTableDataArray, existHeader, existFooter, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableDataArray 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param existHeader  是否存在表头，如果存在则自动提取数据中第一行作为表头
         * @param existFooter  是否存在表尾，如果存在则自动提取数据中最后一行作为表尾
         * @param fontFamily   表格文本内容字体
         */
        public Writer addTable(String[][] pdfTableDataArray, boolean existHeader, boolean existFooter, String fontFamily) {
            List<PdfTable.PdfTableRow> pdfTableRows = Arrays.stream(pdfTableDataArray)
                    .map(pdfTableRowData ->
                            new PdfTable.PdfTableRow().setPdfTableCells(Arrays.stream(pdfTableRowData)
                                    .map(pdfTableRowCell ->
                                            new PdfTable.PdfTableCell().setCellContent(pdfTableRowCell))
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
            return addTable(pdfTableRows, existHeader, existFooter, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableRows 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         */
        public Writer addTable(List<PdfTable.PdfTableRow> pdfTableRows) {
            return addTable(pdfTableRows, false, false, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableRows 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param fontFamily   表格文本内容字体
         */
        public Writer addTable(List<PdfTable.PdfTableRow> pdfTableRows, String fontFamily) {
            return addTable(pdfTableRows, false, false, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableRows 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param existHeader  是否存在表头，如果存在则自动提取数据中第一行作为表头
         * @param existFooter  是否存在表尾，如果存在则自动提取数据中最后一行作为表尾
         */
        public Writer addTable(List<PdfTable.PdfTableRow> pdfTableRows, boolean existHeader, boolean existFooter) {
            return addTable(pdfTableRows, existHeader, existFooter, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableRows 表格完整数据，工具类暂不支持合并操作，即每一行列数均相同，若不相同，那就以最长行为基准填充空值
         * @param existHeader  是否存在表头，如果存在则自动提取数据中第一行作为表头
         * @param existFooter  是否存在表尾，如果存在则自动提取数据中最后一行作为表尾
         * @param fontFamily   表格文本内容字体
         */
        public Writer addTable(List<PdfTable.PdfTableRow> pdfTableRows, boolean existHeader, boolean existFooter, String fontFamily) {
            if (CollectionUtils.isEmpty(pdfTableRows)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "List<PdfTable.PdfTableRow>参数为空");
            }
            // 校准每行填充长度
            int maxSize = 0;
            for (PdfTable.PdfTableRow pdfTableRow : pdfTableRows) {
                if (pdfTableRow.getPdfTableCells().size() > maxSize) {
                    maxSize = pdfTableRow.getPdfTableCells().size();
                }
            }
            if (maxSize != 0) {
                for (PdfTable.PdfTableRow pdfTableRow : pdfTableRows) {
                    int size = pdfTableRow.getPdfTableCells().size();
                    for (int i = 0; i < maxSize - size; i++) {
                        pdfTableRow.getPdfTableCells().add(new PdfTable.PdfTableCell());
                    }
                }
            } else {
                log.warn("表格表体数据缺失，无法渲染表格");
                return this;
            }
            // 构造表体、表头和表尾
            PdfTable.PdfTableBody pdfTableBody = new PdfTable.PdfTableBody();
            PdfTable.PdfTableHeader pdfTableHeader = null;
            PdfTable.PdfTableFooter pdfTableFooter = null;
            if (existHeader) {
                pdfTableHeader = new PdfTable.PdfTableHeader().setPdfTableRows(List.of(pdfTableRows.get(0)));
                pdfTableRows.remove(0);
                if (pdfTableRows.isEmpty()) {
                    log.warn("填充表头后，表格表体数据缺失，无法渲染表格");
                    return this;
                }
            }
            if (existFooter) {
                pdfTableFooter = new PdfTable.PdfTableFooter().setPdfTableRows(List.of(pdfTableRows.get(pdfTableRows.size() - 1)));
                pdfTableRows.remove(pdfTableRows.size() - 1);
                if (pdfTableRows.isEmpty()) {
                    log.warn("填充表尾后，表格表体数据缺失，无法渲染表格");
                    return this;
                }
            }
            pdfTableBody.setPdfTableRows(pdfTableRows);
            return addTable(pdfTableBody, pdfTableHeader, pdfTableFooter, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody PDF表格表体构造类
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody) {
            return addTable(pdfTableBody, null, null, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody PDF表格表体构造类
         * @param fontFamily   表格文本内容字体
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody, String fontFamily) {
            return addTable(pdfTableBody, null, null, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody   PDF表格表体构造类
         * @param pdfTableHeader PDF表格表头构造类
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody, PdfTable.PdfTableHeader pdfTableHeader) {
            return addTable(pdfTableBody, pdfTableHeader, null, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody   PDF表格表体构造类
         * @param pdfTableHeader PDF表格表头构造类
         * @param fontFamily     表格文本内容字体
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody, PdfTable.PdfTableHeader pdfTableHeader, String fontFamily) {
            return addTable(pdfTableBody, pdfTableHeader, null, fontFamily);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody   PDF表格表体构造类
         * @param pdfTableHeader PDF表格表头构造类
         * @param pdfTableFooter PDF表格表尾构造类
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody, PdfTable.PdfTableHeader pdfTableHeader, PdfTable.PdfTableFooter pdfTableFooter) {
            return addTable(pdfTableBody, pdfTableHeader, pdfTableFooter, null);
        }

        /**
         * 添加表格
         *
         * @param pdfTableBody   PDF表格表体构造类
         * @param pdfTableHeader PDF表格表头构造类
         * @param pdfTableFooter PDF表格表尾构造类
         * @param fontFamily     表格文本内容字体
         */
        public Writer addTable(PdfTable.PdfTableBody pdfTableBody, PdfTable.PdfTableHeader pdfTableHeader, PdfTable.PdfTableFooter pdfTableFooter, String fontFamily) {
            return addTable(
                    new PdfTable()
                            .setPdfTableBody(pdfTableBody)
                            .setPdfTableHeader(pdfTableHeader)
                            .setPdfTableFooter(pdfTableFooter)
                            .setFontFamily(fontFamily)
            );
        }

        /**
         * 添加表格
         *
         * @param pdfTable PDF表格构造类
         */
        public Writer addTable(PdfTable pdfTable) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfTable)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfTable参数为空");
            }
            // 设置填充表格的标志符号
            int isAddTable = 0;
            // 设置表格整体属性
            Table table = TemplateHandler.Table.build()
                    // 设置表格字体，字体默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setFontFamily(Objects.isNull(pdfTable.getFontFamily()) ? DEFAULT_FONT_FAMILY : pdfTable.getFontFamily())
                    // 设置表格对齐方式，默认居中
                    .setHorizontalStyle(Objects.isNull(pdfTable.getTableHorizontal()) ? TableHorizontal.CENTER.getName() : pdfTable.getTableHorizontal().getName())
                    // 设置表格上下边距，默认为1
                    .setMarginTop(String.valueOf(Objects.isNull(pdfTable.getMargin()) || pdfTable.getMargin() <= 0 ? 1 : pdfTable.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfTable.getMargin()) || pdfTable.getMargin() <= 0 ? 1 : pdfTable.getMargin()));
            // 设置表体
            TableBody tableBody = TemplateHandler.Table.Body.build();
            if (Objects.nonNull(pdfTable.getPdfTableBody()) && CollectionUtils.isNotEmpty(pdfTable.getPdfTableBody().getPdfTableRows())) {
                List<TableRow> tableBodyRows = pdfTable.getPdfTableBody().getPdfTableRows().stream().map(pdfTableRow -> {
                    TableRow tableRow = new TableRow();
                    pdfTableRow.getPdfTableCells().forEach(pdfTableCell -> {
                        tableRow.addCell(
                                new TableCell()
                                        .addComponent(
                                                TemplateHandler.Text.build()
                                                        // 设置表体单元格文本字号，默认12
                                                        .setFontSize(String.valueOf(Objects.isNull(pdfTableCell.getFontSize()) || pdfTableCell.getFontSize() <= 0 ? 12 : pdfTableCell.getFontSize()))
                                                        // 设置表体单元格文本内容，默认""
                                                        .setText(Objects.isNull(pdfTableCell.getCellContent()) ? "" : pdfTableCell.getCellContent())
                                        )
                                        // 设置表体单元格内容水平对齐方式，默认居中
                                        .setHorizontalStyle(Objects.isNull(pdfTableCell.getCellHorizontal()) ? TableHorizontal.CENTER.getName() : pdfTableCell.getCellHorizontal().getName())
                                        // 设置表体单元格内容垂直对齐方式，默认居中
                                        .setVerticalStyle(Objects.isNull(pdfTableCell.getTableVertical()) ? TableVertical.CENTER.getName() : pdfTableCell.getTableVertical().getName())
                                        // 设置表体单元格边框颜色，默认黑色
                                        .setBorderColor(colorToHex(Objects.isNull(pdfTableCell.getBroderColor()) ? Color.BLACK : pdfTableCell.getBroderColor()))
                                        // 设置表体单元格边框宽度为1
                                        .setBorderWidth("1")
                                        // 设置表体单元格边框样式为实现
                                        .setBorderStyle("solid"));
                    });
                    // 设置表格体表行最小高度为15
                    tableRow.setMinRowHeight("15");
                    return tableRow;
                }).toList();
                tableBody.addRow(tableBodyRows);
                isAddTable += 1;
            }
            // 设置表头
            TableHeader tableHeader = TemplateHandler.Table.Header.build();
            if (Objects.nonNull(pdfTable.getPdfTableHeader()) && CollectionUtils.isNotEmpty(pdfTable.getPdfTableHeader().getPdfTableRows())) {
                List<TableRow> tableHeaderRows = pdfTable.getPdfTableHeader().getPdfTableRows().stream().map(pdfTableRow -> {
                    TableRow tableRow = new TableRow();
                    pdfTableRow.getPdfTableCells().forEach(pdfTableCell -> {
                        tableRow.addCell(
                                new TableCell()
                                        .addComponent(
                                                TemplateHandler.Text.build()
                                                        // 设置表体单元格文本字号，默认12
                                                        .setFontSize(String.valueOf(Objects.isNull(pdfTableCell.getFontSize()) || pdfTableCell.getFontSize() <= 0 ? 12 : pdfTableCell.getFontSize()))
                                                        // 设置表体单元格文本内容，默认""
                                                        .setText(Objects.isNull(pdfTableCell.getCellContent()) ? "" : pdfTableCell.getCellContent())
                                                        // 设置表头单元格内容加粗
                                                        .setFontWeight(FontWeight.BOLD.getName())
                                        )
                                        // 设置表体单元格内容水平对齐方式，默认居中
                                        .setHorizontalStyle(Objects.isNull(pdfTableCell.getCellHorizontal()) ? TableHorizontal.CENTER.getName() : pdfTableCell.getCellHorizontal().getName())
                                        // 设置表体单元格内容垂直对齐方式，默认居中
                                        .setVerticalStyle(Objects.isNull(pdfTableCell.getTableVertical()) ? TableVertical.CENTER.getName() : pdfTableCell.getTableVertical().getName())
                                        // 设置表体单元格边框颜色，默认黑色
                                        .setBorderColor(colorToHex(Objects.isNull(pdfTableCell.getBroderColor()) ? Color.BLACK : pdfTableCell.getBroderColor()))
                                        // 设置表体单元格边框宽度为1
                                        .setBorderWidth("1")
                                        // 设置表体单元格边框样式为实现
                                        .setBorderStyle("solid"));
                    });
                    // 设置表格体表行最小高度为15
                    tableRow.setMinRowHeight("15");
                    return tableRow;
                }).toList();
                tableHeader.addRow(tableHeaderRows);
                isAddTable += 2;
            }
            // 设置表尾
            TableFooter tableFooter = TemplateHandler.Table.Footer.build();
            if (Objects.nonNull(pdfTable.getPdfTableFooter()) && CollectionUtils.isNotEmpty(pdfTable.getPdfTableFooter().getPdfTableRows())) {
                List<TableRow> tableFooterRows = pdfTable.getPdfTableFooter().getPdfTableRows().stream().map(pdfTableRow -> {
                    TableRow tableRow = new TableRow();
                    pdfTableRow.getPdfTableCells().forEach(pdfTableCell -> {
                        tableRow.addCell(
                                new TableCell()
                                        .addComponent(
                                                TemplateHandler.Text.build()
                                                        // 设置表体单元格文本字号，默认12
                                                        .setFontSize(String.valueOf(Objects.isNull(pdfTableCell.getFontSize()) || pdfTableCell.getFontSize() <= 0 ? 12 : pdfTableCell.getFontSize()))
                                                        // 设置表体单元格文本内容，默认""
                                                        .setText(Objects.isNull(pdfTableCell.getCellContent()) ? "" : pdfTableCell.getCellContent())
                                        )
                                        // 设置表体单元格内容水平对齐方式，默认居中
                                        .setHorizontalStyle(Objects.isNull(pdfTableCell.getCellHorizontal()) ? TableHorizontal.CENTER.getName() : pdfTableCell.getCellHorizontal().getName())
                                        // 设置表体单元格内容垂直对齐方式，默认居中
                                        .setVerticalStyle(Objects.isNull(pdfTableCell.getTableVertical()) ? TableVertical.CENTER.getName() : pdfTableCell.getTableVertical().getName())
                                        // 设置表体单元格边框颜色，默认黑色
                                        .setBorderColor(colorToHex(Objects.isNull(pdfTableCell.getBroderColor()) ? Color.BLACK : pdfTableCell.getBroderColor()))
                                        // 设置表体单元格边框宽度为1
                                        .setBorderWidth("1")
                                        // 设置表体单元格边框样式为实现
                                        .setBorderStyle("solid"));
                    });
                    // 设置表格体表行最小高度为15
                    tableRow.setMinRowHeight("15");
                    return tableRow;
                }).toList();
                tableFooter.addRow(tableFooterRows);
                isAddTable += 4;
            }
            // 以二进制做判断依据，进行判断该表格是否需要进行插入数据
            byte[] isAddTableBinArray = String.format("%3s", Integer.toBinaryString(isAddTable)).replace(' ', '0').getBytes();
            // 要求必须包含表体数据，否则不进行填充文本内容操作
            if (isAddTableBinArray[2] == '1') {
                table.setBody(tableBody);
            } else {
                log.warn("表格表体为空，无法渲染表格");
                return this;
            }
            if (isAddTableBinArray[1] == '1') {
                table.setHeader(tableHeader);
            }
            if (isAddTableBinArray[0] == '1') {
                table.setFooter(tableFooter);
            }
            pageList.get(pageIndex).addBodyComponent(table);
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
            return addSplitLine(style, null, null, null, null);
        }

        /**
         * 添加分割线
         *
         * @param style 分割线样式
         * @param color 分割线颜色
         */
        public Writer addSplitLine(SplitLineStyle style, Color color) {
            return addSplitLine(style, color, null, null, null);
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
         * @param pdfSplitLine PDF分割线构造类
         */
        public Writer addSplitLine(PdfSplitLine pdfSplitLine) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfSplitLine)) {
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
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream    图像数据流
         * @param imageExtension 图像扩展名
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
            return addImage(path, null, null, null);
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
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream     图像数据流
         * @param imageExtension  图像扩展名
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
            return addImage(path, imageHorizontal, null, null);
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
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream     图像数据流
         * @param imageExtension  图像扩展名
         * @param imageHorizontal 图像对齐方式
         * @param width           图像宽度
         * @param height          图像高度
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, ImageHorizontal imageHorizontal, Integer width, Integer height) {
            String imageTempPath = copyImageToTempDir(inputStream, imageExtension);
            return addImage(imageTempPath, imageHorizontal, width, height);
        }

        /**
         * 添加图象
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
         * @param pdfImage PDF图像构造类
         */
        public Writer addImage(PdfImage pdfImage) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfImage)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数为空");
            }
            if (!Files.isRegularFile(Path.of(pdfImage.getPath()))) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfImage参数中图像所在路径[path]为不存在或者非文件");
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
                    // 设置图像上下边距，默认1
                    .setMarginTop(String.valueOf(Objects.isNull(pdfImage.getMargin()) || pdfImage.getMargin() <= 0 ? 1 : pdfImage.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfImage.getMargin()) || pdfImage.getMargin() <= 0 ? 1 : pdfImage.getMargin()));
            pageList.get(pageIndex).addBodyComponent(image);
            return this;
        }

        /**
         * 添加条码
         *
         * @param content 条码内容
         * @param barcodeType 条码类型
         */
        public Writer addBarcode(String content, BarcodeType barcodeType) {
            return addBarcode(content, barcodeType, null, null, null);
        }

        /**
         * 添加条码
         *
         * @param content 条码内容
         * @param barcodeType 条码类型
         * @param barcodeHorizontal 条码对齐方式
         */
        public Writer addBarcode(String content, BarcodeType barcodeType, BarcodeHorizontal barcodeHorizontal) {
            return addBarcode(content, barcodeType, barcodeHorizontal, null, null);
        }

        /**
         * 添加条码
         *
         * @param content 条码内容
         * @param barcodeType 条码类型
         * @param barcodeHorizontal 条码对齐方式
         * @param words 条码描述文字
         */
        public Writer addBarcode(String content, BarcodeType barcodeType, BarcodeHorizontal barcodeHorizontal, String words) {
            return addBarcode(content, barcodeType, barcodeHorizontal, words, null);
        }

        /**
         * 添加条码
         *
         * @param content 条码内容
         * @param barcodeType 条码类型
         * @param barcodeHorizontal 条码对齐方式
         * @param words 条码描述文字
         * @param wordsSize 条码描述文字字号
         */
        public Writer addBarcode(String content, BarcodeType barcodeType, BarcodeHorizontal barcodeHorizontal, String words, Integer wordsSize) {
            return addBarcode(
                    new PdfBarcode()
                            .setContent(content)
                            .setBarcodeType(barcodeType)
                            .setBarcodeHorizontal(barcodeHorizontal)
                            .setWords(words)
                            .setWordsSize(wordsSize)
            );
        }

        /**
         * 添加条码
         *
         * @param pdfBarcode PDF条码构造类
         */
        public Writer addBarcode(PdfBarcode pdfBarcode) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (pageList.isEmpty()) {
                addPage();
            }
            if (Objects.isNull(pdfBarcode)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "PdfBarcode参数为空");
            }
            Barcode barcode = TemplateHandler.Barcode.build()
                    // 设置条码类型，默认二维码
                    .setType(Objects.isNull(pdfBarcode.getBarcodeType()) ? BarcodeType.QR_CODE.getName() : pdfBarcode.getBarcodeType().getName())
                    // 设置条码高度，默认100
                    .setHeight((Objects.isNull(pdfBarcode.getHeight()) || pdfBarcode.getHeight() <= 0 ? 100 : pdfBarcode.getHeight()) + "px")
                    // 设置条码宽度，默认100
                    .setWidth((Objects.isNull(pdfBarcode.getWidth()) || pdfBarcode.getWidth() <= 0 ? 100 : pdfBarcode.getWidth()) + "px")
                    // 设置条码内容
                    .setContent(StringUtils.isBlank(pdfBarcode.getContent()) ? null : pdfBarcode.getContent())
                    // 设置条码描述文字
                    .setWords(StringUtils.isBlank(pdfBarcode.getWords()) ? null : pdfBarcode.getWords())
                    // 设置条码描述文字字体，默认SourceHanSansCN，即思源黑体，如果需要自定义默认字体，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改
                    .setWordsFamily(Objects.isNull(pdfBarcode.getWordsFamily()) ? DEFAULT_FONT_FAMILY : pdfBarcode.getWordsFamily())
                    // 设置条码描述文字字号，默认10
                    .setWordsSize((Objects.isNull(pdfBarcode.getWordsSize()) || pdfBarcode.getWordsSize() <= 0 ? 10 : pdfBarcode.getWordsSize()) + "px")
                    // 设置条码对齐方式，默认左对齐
                    .setHorizontalStyle(Objects.isNull(pdfBarcode.getBarcodeHorizontal()) ? BarcodeHorizontal.LEFT.getName() : pdfBarcode.getBarcodeHorizontal().getName())
                    // 设置条码上下边距，默认1
                    .setMarginTop(String.valueOf(Objects.isNull(pdfBarcode.getMargin()) || pdfBarcode.getMargin() <= 0 ? 1 : pdfBarcode.getMargin()))
                    .setMarginBottom(String.valueOf(Objects.isNull(pdfBarcode.getMargin()) || pdfBarcode.getMargin() <= 0 ? 1 : pdfBarcode.getMargin()));
            pageList.get(pageIndex).addBodyComponent(barcode);
            return this;
        }

        /**
         * 将PDF写入响应流
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
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 将PDF写入输出流
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
        private void handlePdfResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".pdf";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".pdf";
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
        private String colorToHex(Color color) {
            if (Objects.isNull(color)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "Color转换为16进制数时，Color对象不能为空");
            }
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }

        /**
         * 将图像拷贝至临时文件夹中
         *
         * @param inputStream    图像数据流
         * @param imageExtension 图像扩展名
         */
        private String copyImageToTempDir(InputStream inputStream, ImageExtension imageExtension) {
            try {
                if (Objects.isNull(imageExtension)) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像扩展名为空");
                }
                String imageTempPath = TEMP_IMAGE_DIR + File.separator + "pdfImage_" + UUID.randomUUID().toString().replace("-", "") + imageExtension.getName();
                Path path = Path.of(imageTempPath);
                Files.deleteIfExists(path);
                FileUtils.copyInputStreamToFile(inputStream, new File(imageTempPath));
                return imageTempPath;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "图像数据拷贝异常");
            }
        }

    }

    /**
     * 读取PDF数据内部类
     */
    public static class Reader {

        /**
         * 初始化文档
         */
        private final org.dromara.pdf.pdfbox.core.base.Document document;

        /**
         * 读取PDF数据内部类构造器
         */
        public Reader(InputStream inputStream) {
            this.document = PdfHandler.getDocumentHandler().load(inputStream);
        }

        /**
         * 获取PDF文档中的段落数据，并写入响应流
         *
         * @param response 响应
         */
        public void getParagraphsResponse(HttpServletResponse response) {
            getParagraphsResponse(null, null, response);
        }

        /**
         * 获取PDF文档中的段落数据，并写入响应流
         *
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getParagraphsResponse(String txtFileName, HttpServletResponse response) {
            getParagraphsResponse(null, txtFileName, response);
        }

        /**
         * 获取PDF文档中的段落数据，并写入响应流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getParagraphsResponse(Integer pageIndex, String txtFileName, HttpServletResponse response) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理TXT类型响应
                handleTxtResponse(txtFileName, response);
                getParagraphsTxt(pageIndex, outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 获取PDF文档中的段落数据，并写入输出流
         *
         * @param outputStream 输出流
         */
        public void getParagraphsTxt(OutputStream outputStream) {
            getParagraphsTxt(null, outputStream);
        }

        /**
         * 获取PDF文档中的段落数据，并写入输出流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param outputStream 输出流
         */
        public void getParagraphsTxt(Integer pageIndex, OutputStream outputStream) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取段落数据时，输出流不能为空");
            }
            List<String> paragraphList = getParagraphsList(pageIndex);
            // 遍历段落列表，同时将数据传至输出流
            paragraphList.forEach(paragraph -> {
                try {
                    outputStream.write(paragraph.getBytes(StandardCharsets.UTF_8));
                    outputStream.write('\n');
                } catch (IOException e) {
                    throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取段落数据时，写入输出流异常");
                }
            });
        }

        /**
         * 获取PDF文档中的段落数据
         */
        public List<String> getParagraphsList() {
            return getParagraphsList(null);
        }

        /**
         * 获取PDF文档中的段落数据
         *
         * @param pageIndex PDF页码索引，从0开始
         */
        public List<String> getParagraphsList(Integer pageIndex) {
            try {
                // 创建结果集
                List<String> result = new ArrayList<>();
                if (Objects.isNull(pageIndex)) {
                    for (int i = 0; i < document.getTotalPageNumber(); i++) {
                        // 创建PDF文档分析器
                        DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(document);
                        // 获取当前页的段落信息Set
                        Set<TextInfo> textInfos = documentAnalyzer.analyzeText(i);
                        // 将段落信息Set排序后，遍历添加至结果集
                        textInfos.stream().sorted((text1, text2) -> {
                            try {
                                double height1 = Double.parseDouble(text1.getTextBeginPosition().split(",")[1]);
                                double height2 = Double.parseDouble(text2.getTextBeginPosition().split(",")[1]);
                                return Double.compare(height2, height1);
                            } catch (NumberFormatException e) {
                                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "段落位置获取或转换失败");
                            }
                        }).forEach(textInfo -> {
                            String textContent = handleDigitalDecoding(textInfo.getTextContent());
                            result.add(textContent);
                        });
                    }
                } else {
                    if (document.getTotalPageNumber() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "读取PDF中的段落时，无法解析非法页码数");
                    }
                    // 创建PDF文档分析器
                    DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(document);
                    // 获取当前页的段落信息Set
                    Set<TextInfo> textInfos = documentAnalyzer.analyzeText(pageIndex);
                    // 将段落信息Set排序后，遍历添加至结果集
                    textInfos.stream().sorted((text1, text2) -> {
                        try {
                            double height1 = Double.parseDouble(text1.getTextBeginPosition().split(",")[1]);
                            double height2 = Double.parseDouble(text2.getTextBeginPosition().split(",")[1]);
                            return Double.compare(height2, height1);
                        } catch (NumberFormatException e) {
                            throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "段落位置获取或转换失败");
                        }
                    }).forEach(textInfo -> {
                        String textContent = handleDigitalDecoding(textInfo.getTextContent());
                        result.add(textContent);
                    });
                }
                return result;
            } finally {
                // 关闭文档
                document.close();
            }
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param response 响应
         */
        public void getImagesResponse(HttpServletResponse response) {
            getImagesResponse(null, null, response, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param response 响应
         */
        public void getImagesResponse(Integer pageIndex, HttpServletResponse response) {
            getImagesResponse(pageIndex, null, response, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response) {
            getImagesResponse(null, zipFileName, response, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getImagesResponse(Integer pageIndex, String zipFileName, HttpServletResponse response) {
            getImagesResponse(pageIndex, zipFileName, response, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response, Integer zipLevel) {
            getImagesResponse(null, zipFileName, response, zipLevel);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesResponse(Integer pageIndex, String zipFileName, HttpServletResponse response, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getImagesZip(pageIndex, outputStream, zipLevel);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为输出流
         *
         * @param outputStream 输出流
         */
        public void getImagesZip(OutputStream outputStream) {
            getImagesZip(null, outputStream, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为输出流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param outputStream 输出流
         */
        public void getImagesZip(Integer pageIndex, OutputStream outputStream) {
            getImagesZip(pageIndex, outputStream, null);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为输出流
         *
         * @param outputStream 输出流
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesZip(OutputStream outputStream, Integer zipLevel) {
            getImagesZip(null, outputStream, zipLevel);
        }

        /**
         * 获取PDF文档中的图像数据，并且进行压缩，将压缩后的数据转为输出流
         *
         * @param pageIndex PDF页码索引，从0开始
         * @param outputStream 输出流
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesZip(Integer pageIndex, OutputStream outputStream, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "读取PDF中的图像时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                Map<String, List<byte[]>> imagesByteArray = getImagesByteArray(pageIndex);
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                for (Map.Entry<String, List<byte[]>> stringListEntry : imagesByteArray.entrySet()) {
                    // 根据图像扩展名进行遍历
                    String extension = stringListEntry.getKey();
                    List<byte[]> imageArrayList = stringListEntry.getValue();
                    // 将每张图像byte数组数据传至压缩输出流中
                    for (byte[] image : imageArrayList) {
                        String entryName = uuid + "_" + index + "." + extension;
                        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                        zipArchiveOutputStream.putArchiveEntry(entry);
                        ByteBuf buf = Unpooled.copiedBuffer(image);
                        buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                        zipArchiveOutputStream.closeArchiveEntry();
                        index++;
                    }
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "读取PDF中的图像时，压缩发生异常");
            }
        }

        /**
         * 获取PDF文档中的图像数据，同时按照扩展名分类，并转换为byte[]集合类型
         */
        public Map<String, List<byte[]>> getImagesByteArray() {
            return getImagesByteArray(null);
        }

        /**
         * 获取PDF文档中的图像数据，同时按照扩展名分类，并转换为byte[]集合类型
         *
         * @param pageIndex PDF页码索引，从0开始
         */
        public Map<String, List<byte[]>> getImagesByteArray(Integer pageIndex) {
            try {
                // 创建结果集
                Map<String, List<byte[]>> result = new HashMap<>();
                if (Objects.isNull(pageIndex)) {
                    for (int i = 0; i < document.getTotalPageNumber(); i++) {
                        // 创建PDF文档分析器
                        DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(document);
                        // 获取当前页的图像信息Set
                        Set<ImageInfo> imageInfos = documentAnalyzer.analyzeImage(i);
                        // 遍历图像信息
                        for (ImageInfo imageInfo : imageInfos) {
                            // 创建临时Byte数组输出流
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            // 获取图像扩展名
                            String extension = imageInfo.getImageType();
                            // 将图像信息数据写入输出流中
                            ImageIO.write(imageInfo.getImage(), extension, byteArrayOutputStream);
                            // 封装结果集
                            if (Objects.isNull(result.get(extension))) {
                                ArrayList<byte[]> value = new ArrayList<>();
                                value.add(byteArrayOutputStream.toByteArray());
                                result.put(extension, value);
                            } else {
                                result.get(extension).add(byteArrayOutputStream.toByteArray());
                            }
                        }
                    }
                } else {
                    if (document.getTotalPageNumber() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "读取PDF中的图像时，无法解析非法页码数");
                    }
                    // 创建PDF文档分析器
                    DocumentAnalyzer documentAnalyzer = new DocumentAnalyzer(document);
                    // 获取当前页的图像信息Set
                    Set<ImageInfo> imageInfos = documentAnalyzer.analyzeImage(pageIndex);
                    // 遍历图像信息
                    for (ImageInfo imageInfo : imageInfos) {
                        // 创建临时Byte数组输出流
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        // 获取图像扩展名
                        String extension = imageInfo.getImageType();
                        // 将图像信息数据写入输出流中
                        ImageIO.write(imageInfo.getImage(), extension, byteArrayOutputStream);
                        // 封装结果集
                        if (Objects.isNull(result.get(extension))) {
                            ArrayList<byte[]> value = new ArrayList<>();
                            value.add(byteArrayOutputStream.toByteArray());
                            result.put(extension, value);
                        } else {
                            result.get(extension).add(byteArrayOutputStream.toByteArray());
                        }
                    }
                }
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PDF_FILE_ERROR, "读取PDF中的图像时，发生异常");
            } finally {
                // 关闭文档
                document.close();
            }
        }

        /**
         * 段落数字解码
         *
         * @param paragraph 段落
         */
        private String handleDigitalDecoding(String paragraph) {
            return paragraph.replace("\uE000", "1")
                    .replace("\uE001", "2")
                    .replace("\uE002", "3")
                    .replace("\uE003", "4")
                    .replace("\uE004", "5")
                    .replace("\uE005", "6")
                    .replace("\uE006", "7")
                    .replace("\uE007", "8")
                    .replace("\uE008", "9")
                    .replace("\uE009", "0");
        }

        /**
         * 处理ContentType是Txt格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleTxtResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".txt";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".txt";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("text/plain;charset=UTF-8");
        }

        /**
         * 处理ContentType是Zip格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleZipResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".zip";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".zip";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/zip;charset=UTF-8");
        }

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
         * 页面字号
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
     * PDF水印构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfWatermark {

        /**
         * 水印ID
         */
        private String id;

        /**
         * 水印文本列表，列表元素个数对应文本行数
         */
        private List<String> texts;

        /**
         * 水印文本字体
         */
        private String fontFamily;

        /**
         * 水印文本字号
         */
        private Integer fontSize;

        /**
         * 水印文本透明度
         */
        private Integer fontAlpha;

        /**
         * 水印文本宽度
         */
        private Integer width;

        /**
         * 水印文本高度
         */
        private Integer height;

        /**
         * 水印文本显示宽度
         */
        private Integer showWidth;

        public PdfWatermark setTextArray(String... texts) {
            this.texts = Arrays.stream(texts).toList();
            return this;
        }

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
         * 段落字号
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
         * 段落上下边距
         */
        private Integer margin;

        /**
         * 段落对齐方式
         */
        private ParagraphHorizontal paragraphHorizontal;

    }

    /**
     * PDF表格构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfTable {

        /**
         * 表格表体
         */
        private PdfTableBody pdfTableBody;

        /**
         * 表格表头
         */
        private PdfTableHeader pdfTableHeader;

        /**
         * 表格表尾
         */
        private PdfTableFooter pdfTableFooter;

        /**
         * 表格文本内容字体
         */
        private String fontFamily;

        /**
         * 表格对齐方式
         */
        private TableHorizontal tableHorizontal;

        /**
         * 表格上下边距
         */
        private Integer margin;

        /**
         * PDF表格表头构造类
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Accessors(chain = true)
        public static class PdfTableHeader {

            /**
             * 表格表行列表
             */
            private List<PdfTableRow> pdfTableRows;

        }

        /**
         * PDF表格表体构造类
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Accessors(chain = true)
        public static class PdfTableBody {

            /**
             * 表格表行列表
             */
            private List<PdfTableRow> pdfTableRows;

        }

        /**
         * PDF表格表尾构造类
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Accessors(chain = true)
        public static class PdfTableFooter {

            /**
             * 表格表行列表
             */
            private List<PdfTableRow> pdfTableRows;

        }

        /**
         * PDF表格表行构造类
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Accessors(chain = true)
        public static class PdfTableRow {

            /**
             * 表格单元格列表
             */
            private List<PdfTableCell> pdfTableCells = new ArrayList<>();

        }

        /**
         * PDF表格单元格构造类
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Accessors(chain = true)
        public static class PdfTableCell {

            /**
             * 单元格内容
             */
            private String cellContent;

            /**
             * 单元格内容字号
             */
            private Integer fontSize;

            /**
             * 单元格内容水平对齐方式
             */
            private TableHorizontal cellHorizontal;

            /**
             * 单元格内容垂直对齐方式
             */
            private TableVertical tableVertical;

            /**
             * 单元格边框颜色
             */
            private Color broderColor;

        }

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

    /**
     * PDF条码构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PdfBarcode {

        /**
         * 条码类型
         */
        private BarcodeType barcodeType;

        /**
         * 条码高度
         */
        private Integer height;

        /**
         * 条码宽度
         */
        private Integer width;

        /**
         * 条码内容
         */
        private String content;

        /**
         * 条码描述文字
         */
        private String words;

        /**
         * 条码描述文字字体
         */
        private String wordsFamily;

        /**
         * 条码描述文字字号
         */
        private Integer wordsSize;

        /**
         * 条码对齐方式
         */
        private BarcodeHorizontal barcodeHorizontal;

        /**
         * 条码上下边距
         */
        private Integer margin;

    }

}
