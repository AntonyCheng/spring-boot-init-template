package top.sharehome.springbootinittemplate.utils.document.word;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.deepoove.poi.XWPFTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;
import top.sharehome.springbootinittemplate.utils.document.word.enums.ImageExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;

/**
 * Word工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class WordUtils {

    /**
     * 输出Word模板内部类
     * 该导出模板是基于POI-TL框架的封装，该框架主要以"{{XXX}}"型标签为内容插入点，具体用法查看https://deepoove.com/poi-tl
     */
    public static class Template {

        /**
         * 导出Word模板目录下的模板文件到响应流，模板目录一定是resources文件夹下templates/word目录
         *
         * @param templateName 模板名称（需要带上扩展名）
         * @param tagMap       标签Map
         * @param filename     导出文件名称
         * @param response     响应流
         */
        public void export(String templateName, Map<String, Object> tagMap, String filename, HttpServletResponse response) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                handleWordResponse(filename, response);
                export(templateName, tagMap, outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 导出Word模板目录下的模板文件到输出流，模板目录一定是resources文件夹下templates/word目录
         *
         * @param templateName 模板名称（需要带上扩展名）
         * @param tagMap       标签Map
         * @param outputStream 输出流
         */
        public void export(String templateName, Map<String, Object> tagMap, OutputStream outputStream) {
            try {
                if (Objects.isNull(outputStream)) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "模板文件输出时，输出流为空");
                }
                if (Objects.isNull(tagMap)) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "模板文件输出时，标签Map为空");
                }
                String extension = FilenameUtils.getExtension(templateName);
                if (!Objects.equals(extension, "docx") && !Objects.equals(extension, "doc")) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "指定模板文件扩展名不正确");
                }
                ClassPathResource classPathResource = new ClassPathResource("templates/word/" + templateName);
                if (StringUtils.isBlank(templateName) || !classPathResource.exists()) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "模板文件[" + templateName + "]未找到");
                }
                InputStream inputStream = classPathResource.getInputStream();
                XWPFTemplate template = XWPFTemplate.compile(inputStream).render(tagMap);
                template.writeAndClose(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "模板导出时，数据流处理错误");
            }
        }

        /**
         * 处理ContentType是Word格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleWordResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            String baseName = FilenameUtils.getBaseName(fileName);
            String extension = FilenameUtils.getExtension(fileName);
            if (StringUtils.isBlank(baseName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + extension;
            } else {
                realName = baseName + "_" + UUID.randomUUID().toString().replace("-", "") + extension;
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8");
        }

    }

    /**
     * 写入Word数据内部类
     */
    public static class Writer {

        /**
         * 初始化文档
         */
        private final XWPFDocument document;

        /**
         * 写入Word数据内部类构造器
         */
        public Writer() {
            document = new XWPFDocument();
        }

        /**
         * 添加页面
         */
        public Writer addPage() {
            document.createParagraph().createRun().addBreak(BreakType.PAGE);
            return this;
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         */
        public Writer addParagraph(String textarea) {
            return addParagraph(textarea, null, null, null, null, null);
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param isPageBreak 是否另起一页
         */
        public Writer addParagraph(String textarea, Boolean isPageBreak) {
            return addParagraph(textarea, null, null, null, null, isPageBreak);
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param fontSize 字号
         * @param isPageBreak 是否另起一页
         */
        public Writer addParagraph(String textarea, Integer fontSize, Boolean isPageBreak) {
            return addParagraph(textarea, fontSize, null, null, null, isPageBreak);
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param fontSize 字号
         * @param fontColor 字体颜色
         * @param isPageBreak 是否另起一页
         */
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor, Boolean isPageBreak) {
            return addParagraph(textarea, fontSize, fontColor, null, null, isPageBreak);
        }

        /**
         * 添加段落
         *
         * @param textarea 文本内容
         * @param fontSize 字号
         * @param fontColor 字体颜色
         * @param isBold 是否粗体
         * @param isItalic 是否斜体
         * @param isPageBreak 是否另起一页
         */
        public Writer addParagraph(String textarea, Integer fontSize, Color fontColor, Boolean isBold, Boolean isItalic, Boolean isPageBreak) {
            return addParagraph(
                    new WordParagraph()
                            .setTextContent(textarea)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
                            .setIsPageBreak(isPageBreak)
            );
        }

        /**
         * 添加段落
         *
         * @param wordParagraph Word段落构造类
         */
        public Writer addParagraph(WordParagraph wordParagraph) {
            if (Objects.isNull(wordParagraph)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "WordParagraph参数为空");
            }
            // 创建段落
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            // 设置段落文本
            run.setText(Objects.isNull(wordParagraph.getTextContent()) ? "" : wordParagraph.getTextContent());
            // 设置段落字体，字体默认SimHei，即黑体
            run.setFontFamily(Objects.isNull(wordParagraph.getFontFamily()) ? "SimHei" : wordParagraph.getFontFamily());
            // 设置段落字号，默认12
            run.setFontSize(Objects.isNull(wordParagraph.getFontSize()) || wordParagraph.getFontSize() <= 0 ? 12 : wordParagraph.getFontSize());
            // 设置段落是否斜体，默认否
            run.setItalic(Objects.isNull(wordParagraph.getIsItalic()) ? Boolean.FALSE : wordParagraph.getIsItalic());
            // 设置段落是否粗体，默认否
            run.setBold(Objects.isNull(wordParagraph.getIsBold()) ? Boolean.FALSE : wordParagraph.getIsBold());
            // 设置段落颜色，默认黑色
            run.setColor(colorToHex(Objects.isNull(wordParagraph.getFontColor()) ? Color.BLACK : wordParagraph.getFontColor()));
            // 设置段落是否首行缩进，默认否
            if (Objects.nonNull(wordParagraph.getIsIndent()) && wordParagraph.getIsIndent()) {
                paragraph.setIndentationFirstLine(wordParagraph.getFontSize() * 40);
            }
            // 设置段落行间距，默认为1
            paragraph.setSpacingBetween(Objects.isNull(wordParagraph.getSpacingBetween()) || wordParagraph.getSpacingBetween() <= 0 ? 1 : wordParagraph.getSpacingBetween(), LineSpacingRule.AUTO);
            // 设置段落对齐方式，默认两端对齐
            paragraph.setAlignment(Objects.isNull(wordParagraph.getAlignment()) ? ParagraphAlignment.BOTH : wordParagraph.getAlignment());
            // 设置段落下划线，默认没有下划线
            run.setUnderline(Objects.isNull(wordParagraph.getUnderlineType()) ? UnderlinePatterns.NONE : wordParagraph.getUnderlineType());
            // 设置段落段前是否另起一页，默认不另起一页
            paragraph.setPageBreak(Objects.isNull(wordParagraph.getIsPageBreak()) ? Boolean.FALSE : wordParagraph.getIsPageBreak());
            return this;
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         */
        public Writer addTable(WordTable.TableMap tableMap) {
            return addTable(tableMap, null, null, null, null, null);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param isPageBreak 表格前是否另起一页
         */
        public Writer addTable(WordTable.TableMap tableMap, Boolean isPageBreak) {
            return addTable(tableMap, null, null, null, null, isPageBreak);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param alignment 表格对齐方式
         * @param isPageBreak 表格前是否另起一页
         */
        public Writer addTable(WordTable.TableMap tableMap, TableRowAlign alignment, Boolean isPageBreak) {
            return addTable(tableMap, null, null, null, alignment, isPageBreak);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param width 表格宽度
         * @param alignment 表格对齐方式
         * @param isPageBreak 表格前是否另起一页
         */
        public Writer addTable(WordTable.TableMap tableMap, Integer width, TableRowAlign alignment, Boolean isPageBreak) {
            return addTable(tableMap, width, null, null, alignment, isPageBreak);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         * @param width 表格宽度
         * @param alignment 表格对齐方式
         * @param isPageBreak 表格前是否另起一页
         */
        public Writer addTable(WordTable.TableMap tableMap, Integer rowNum, Integer columnNum, Integer width, TableRowAlign alignment, Boolean isPageBreak) {
            return addTable(
                    new WordTable()
                            .setTableMap(tableMap)
                            .setRowNum(rowNum)
                            .setColumnNum(columnNum)
                            .setWidth(width)
                            .setAlignment(alignment)
                            .setIsPageBreak(isPageBreak)
            );
        }

        /**
         * 添加表格
         *
         * @param wordTable Word表格构造类
         */
        public Writer addTable(WordTable wordTable) {
            if (Objects.isNull(wordTable)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "WordTable参数为空");
            }
            // 设置表格段前是否另起一页，默认不另起一页
            if (Objects.nonNull(wordTable.getIsPageBreak()) && wordTable.getIsPageBreak()) {
                document.createParagraph().createRun().addBreak(BreakType.PAGE);
            }
            // 如果输入预设行列值异常，那就直接返回即可
            if ((Objects.nonNull(wordTable.getRowNum()) && wordTable.getRowNum() <= 0) || (Objects.nonNull(wordTable.getColumnNum()) && wordTable.getColumnNum() <= 0)) {
                return this;
            }
            // 由表格内容获取表格本身的行列值
            Map<Integer, List<String>> map = wordTable.getTableMap().getMap();
            int maxRow = -1;
            int maxColumn = -1;
            for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                if (entry.getKey() > maxRow) {
                    maxRow = entry.getKey();
                }
                if (entry.getValue().size() > maxColumn) {
                    maxColumn = entry.getValue().size();
                }
            }
            // 如果表格本身无行值，则说明表格内容为空，直接返回即可
            if (maxRow == -1) {
                return this;
            } else {
                // 先把行值从索引值改为真实数值
                maxRow++;
                // 如果表格本身有行值，那就和预设值作比较，判断预设值是否是无效值
                if (Objects.nonNull(wordTable.getRowNum()) && maxRow < wordTable.getRowNum()) {
                    maxRow = wordTable.getRowNum();
                }
                if (Objects.nonNull(wordTable.getColumnNum()) && maxColumn < wordTable.getColumnNum()) {
                    maxColumn = wordTable.getColumnNum();
                }
            }
            // 填充表格
            XWPFTable table = document.createTable(maxRow, maxColumn);
            map.forEach((k, v) -> {
                for (int i = 0; i < v.size(); i++) {
                    table.getRow(k).getCell(i).setText(v.get(i));
                }
            });
            // 设置表格格式
            table.setWidth(Objects.isNull(wordTable.getWidth()) || wordTable.getWidth() <= 0 ? 8000 : wordTable.getWidth());
            table.setTableAlignment(Objects.isNull(wordTable.getAlignment()) ? TableRowAlign.CENTER : wordTable.getAlignment());
            return this;
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         */
        public Writer addImage(MultipartFile multipartFile) {
            return addImage(multipartFile, null, null, null, null);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         */
        public Writer addImage(InputStream inputStream) {
            return addImage(inputStream, null, null, null, null, null);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param imageExtension 图像类型
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension) {
            return addImage(inputStream, imageExtension, null, null, null, null);
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(MultipartFile multipartFile, Boolean isPageBreak) {
            return addImage(multipartFile, null, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, Boolean isPageBreak) {
            return addImage(inputStream, null, null, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param imageExtension 图像类型
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, Boolean isPageBreak) {
            return addImage(inputStream, imageExtension, null, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         * @param alignment 图像段落对齐方式
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(MultipartFile multipartFile, ParagraphAlignment alignment, Boolean isPageBreak) {
            return addImage(multipartFile, alignment, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param alignment 图像段落对齐方式
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, ParagraphAlignment alignment, Boolean isPageBreak) {
            return addImage(inputStream, null, alignment, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param imageExtension 图像类型
         * @param alignment 图像段落对齐方式
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, ParagraphAlignment alignment, Boolean isPageBreak) {
            return addImage(inputStream, imageExtension, alignment, null, null, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         * @param alignment 图像段落对齐方式
         * @param width 图像宽度
         * @param height 图像高度
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(MultipartFile multipartFile, ParagraphAlignment alignment, Integer width, Integer height, Boolean isPageBreak) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                String extension = FilenameUtils.getExtension(StringUtils.isNotBlank(multipartFile.getOriginalFilename()) ? multipartFile.getOriginalFilename() : multipartFile.getName());
                ImageExtension imageExtension = ImageExtension.getEnumByName("." + extension);
                return addImage(inputStream, imageExtension, alignment, width, height, isPageBreak);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param alignment 图像段落对齐方式
         * @param width 图像宽度
         * @param height 图像高度
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, ParagraphAlignment alignment, Integer width, Integer height, Boolean isPageBreak) {
            return addImage(inputStream, null, alignment, width, height, isPageBreak);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param imageExtension 图像类型
         * @param alignment 图像段落对齐方式
         * @param width 图像宽度
         * @param height 图像高度
         * @param isPageBreak 图像前是否另起一页
         */
        public Writer addImage(InputStream inputStream, ImageExtension imageExtension, ParagraphAlignment alignment, Integer width, Integer height, Boolean isPageBreak) {
            return addImage(
                    new WordImage()
                            .setInputStream(inputStream)
                            .setImageExtension(imageExtension)
                            .setAlignment(alignment)
                            .setWidth(width)
                            .setHeight(height)
                            .setIsPageBreak(isPageBreak)
            );
        }

        /**
         * 添加图像
         *
         * @param wordImage Word图像构造类
         */
        public Writer addImage(WordImage wordImage) {
            try {
                if (Objects.isNull(wordImage)) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "WordImage参数为空");
                }
                // 如果连数据流都没有，那就直接返回即可
                if (Objects.isNull(wordImage.getInputStream())) {
                    return this;
                }
                XWPFParagraph paragraph = document.createParagraph();
                // 添加图像的前提就是存在一个段落
                XWPFRun run = paragraph.createRun();
                // 先获取图像信息
                int imgHeight = ImageIO.read(wordImage.getInputStream()).getHeight();
                wordImage.getInputStream().reset();
                int imgWidth = ImageIO.read(wordImage.getInputStream()).getWidth();
                wordImage.getInputStream().reset();
                // 添加图像，默认原长宽的JPEG格式图片
                ImageExtension imageExtension = Objects.isNull(wordImage.getImageExtension()) ? ImageExtension.JPG : wordImage.getImageExtension();
                String filename = StringUtils.isEmpty(wordImage.getFilename()) ? UUID.randomUUID().toString().replace("-", "") : wordImage.getFilename();
                int width = Objects.isNull(wordImage.getWidth()) || wordImage.getWidth() <= 0 ? imgWidth : wordImage.getWidth();
                int height = Objects.isNull(wordImage.getHeight()) || wordImage.getHeight() <= 0 ? imgHeight : wordImage.getHeight();
                run.addPicture(wordImage.getInputStream(), imageExtension.getOoxmlId(), filename, Units.toEMU(width), Units.toEMU(height));
                // 设置图像对齐方式，默认左对齐
                paragraph.setAlignment(Objects.isNull(wordImage.getAlignment()) ? ParagraphAlignment.LEFT : wordImage.getAlignment());
                // 设置图像上下边距，默认1
                paragraph.setSpacingBetween(Objects.isNull(wordImage.getMargin()) || wordImage.getMargin() <= 0 ? 1 : wordImage.getMargin(), LineSpacingRule.AUTO);
                // 设置图像段前是否另起一页，默认不另起一页
                paragraph.setPageBreak(Objects.isNull(wordImage.getIsPageBreak()) ? Boolean.FALSE : wordImage.getIsPageBreak());
            } catch (IOException | InvalidFormatException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "添加图像异常");
            }
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
                handleWordResponse(fileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                doWrite(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 将Word写入输出流
         *
         * @param outputStream 输出流
         */
        public void doWrite(OutputStream outputStream) {
            try (outputStream) {
                document.write(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "写入输出流异常");
            }
        }

        /**
         * 处理ContentType是Word格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleWordResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
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
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8");
        }

        /**
         * 将Color转换成16进制数
         */
        private String colorToHex(Color color) {
            if (Objects.isNull(color)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "Color转换为16进制数时，Color对象不能为空");
            }
            return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }

    }

    /**
     * 读取Word数据内部类
     */
    public static class Reader {

        /**
         * 初始化文档
         */
        private final XWPFDocument document;

        /**
         * 读取Word数据内部类构造器
         */
        public Reader(InputStream inputStream) {
            try {
                if (Objects.isNull(inputStream) || Objects.equals(inputStream.available(), 0)) {
                    throw new IOException();
                }
                this.document = new XWPFDocument(inputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "解析文档异常");
            }
        }

        /**
         * 获取Word文档中的段落数据，并写入响应流
         *
         * @param response    响应流
         */
        public void getParagraphsResponse(HttpServletResponse response) {
            getParagraphsResponse(null, response);
        }

        /**
         * 获取Word文档中的段落数据，并写入响应流
         *
         * @param txtFileName    TXT文件名
         * @param response    响应流
         */
        public void getParagraphsResponse(String txtFileName, HttpServletResponse response) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理TXT类型响应
                handleTxtResponse(txtFileName, response);
                getParagraphsTxt(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取段落数据时，写入输出流异常");
            }
        }

        /**
         * 获取Word文档中的段落数据，并写入输出流
         *
         * @param outputStream 输出流
         */
        public void getParagraphsTxt(OutputStream outputStream) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取段落数据时，输出流不能为空");
            }
            List<String> paragraphsList = getParagraphsList();
            // 遍历段落列表，同时将数据传至输出流
            paragraphsList.forEach(paragraph -> {
                try {
                    outputStream.write(paragraph.getBytes(StandardCharsets.UTF_8));
                    outputStream.write('\n');
                } catch (IOException e) {
                    throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取段落数据时，写入输出流异常");
                }
            });
        }

        /**
         * 获取Word文档中的段落数据
         */
        public List<String> getParagraphsList() {
            try {
                List<String> list = document.getParagraphs()
                        .stream()
                        .map(XWPFParagraph::getText)
                        .filter(ObjectUtils::isNotEmpty)
                        .toList();
                // 关闭文档
                document.close();
                return list;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取段落数据时，关闭输出流异常");
            }
        }

        /**
         * 获取Word文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param response 响应
         */
        public void getImagesResponse(HttpServletResponse response) {
            getImagesResponse(null, response, null);
        }

        /**
         * 获取Word文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response) {
            getImagesResponse(zipFileName, response, null);
        }

        /**
         * 获取Word文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getImagesZip(outputStream, zipLevel);
                // 刷新响应流
                outputStream.flush();
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从Word数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         */
        public void getImagesZip(OutputStream outputStream) {
            getImagesZip(outputStream, null);
        }

        /**
         * 从Word数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getImagesZip(OutputStream outputStream, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "读取Word中的图像时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                Map<String, List<byte[]>> imagesByteArray = getImagesByteArray();
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
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "读取Word中的图像时，压缩发生异常");
            }
        }

        /**
         * 获取Word文档中的图像数据，同时按照扩展名分类，并转换为byte[]集合类型
         */
        public Map<String, List<byte[]>> getImagesByteArray() {
            try {
                // 创建结果集
                Map<String, List<byte[]>> result = new HashMap<>();
                // 获取所有图像信息List
                List<XWPFPictureData> imageInfos = document.getAllPictures();
                // 遍历图像信息
                for (XWPFPictureData imageInfo : imageInfos) {
                    // 获取图像byte[]数据
                    byte[] data = imageInfo.getData();
                    // 获取图像扩展名
                    String extension = imageInfo.getPictureTypeEnum().getExtension().replace(".", "");
                    // 封装结果集
                    if (Objects.isNull(result.get(extension))) {
                        ArrayList<byte[]> value = new ArrayList<>();
                        value.add(data);
                        result.put(extension, value);
                    } else {
                        result.get(extension).add(data);
                    }
                }
                // 关闭文档
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取图像数据时，关闭输出流异常");
            }
        }

        /**
         * 从Word中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param response 响应
         */
        public void getTablesResponse(HttpServletResponse response) {
            getTablesResponse(null, response, null, null);
        }

        /**
         * 从Word中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response) {
            getTablesResponse(zipFileName, response, null, null);
        }

        /**
         * 从Word中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param excelType    表格格式
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType) {
            getTablesResponse(zipFileName, response, excelType, null);
        }

        /**
         * 从Word中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getTablesZip(outputStream, excelType, zipLevel);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从Word数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         */
        public void getTablesZip(OutputStream outputStream) {
            getTablesZip(outputStream, null, null);
        }

        /**
         * 从Word数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType) {
            getTablesZip(outputStream, excelType, null);
        }

        /**
         * 从Word中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "读取Word中的表格时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                List<byte[]> tables = getTablesByteArray(excelType);
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                // 将每个表格byte数组数据传至压缩输出流中
                for (byte[] picture : tables) {
                    String entryName = uuid + "_" + index + (Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).getValue();
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    zipArchiveOutputStream.putArchiveEntry(entry);
                    ByteBuf buf = Unpooled.copiedBuffer(picture);
                    buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                    index++;
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "读取Word中的表格时，压缩发生异常");
            }
        }

        /**
         * 从Word中获取表格数据流，默认封装成xlsx格式文件Byte[]数据
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param excelType 封装后的Excel文件扩展名
         */
        public List<byte[]> getTablesByteArray(ExcelTypeEnum excelType) {
            try {
                // 获取表格，遍历每一行单元格数据，封装结果集
                List<byte[]> list = document.getTables().stream().map(table -> {
                    List<List<String>> tableList = new ArrayList<>();
                    int numberOfRows = table.getNumberOfRows();
                    for (int i = 0; i < numberOfRows; i++) {
                        List<String> cellList = table.getRow(i).getTableCells().stream().map(XWPFTableCell::getText).toList();
                        tableList.add(cellList);
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                    return byteArrayOutputStream.toByteArray();
                }).toList();
                // 关闭文档
                document.close();
                return list;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取表格数据时，关闭输出流异常");
            }
        }

        /**
         * 从Word数据流中获取表格文本数据，并封装成TableMap类型对象列表
         */
        public List<WordTable.TableMap> getTablesMaps() {
            try {
                // 获取表格，遍历每一行单元格数据，封装结果集
                List<XWPFTable> tables = document.getTables();
                List<WordTable.TableMap> tableMaps = tables.stream().map(table -> {
                    WordTable.TableMap tableMap = new WordTable.TableMap();
                    int numberOfRows = table.getNumberOfRows();
                    for (int i = 0; i < numberOfRows; i++) {
                        List<String> cellList = table.getRow(i).getTableCells().stream().map(XWPFTableCell::getText).toList();
                        tableMap.put(cellList);
                    }
                    return tableMap;
                }).toList();
                // 关闭文档
                document.close();
                return tableMaps;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.WORD_FILE_ERROR, "获取表格数据时，关闭输出流异常");
            }
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
     * Word段落构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WordParagraph {

        /**
         * 段落文字内容
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
         * 段落是否加粗
         */
        private Boolean isBold;

        /**
         * 段落是否斜体
         */
        private Boolean isItalic;

        /**
         * 段落文字颜色
         */
        private Color fontColor;

        /**
         * 段落是否首行缩进
         */
        private Boolean isIndent;

        /**
         * 段落下划线类型
         */
        private UnderlinePatterns underlineType;

        /**
         * 段落行间距
         */
        private Integer spacingBetween;

        /**
         * 段落对齐方式
         */
        private ParagraphAlignment alignment;

        /**
         * 段落前是否另起一页
         */
        private Boolean isPageBreak;

    }

    /**
     * Word图像构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WordImage {

        /**
         * 图像数据输入流
         */
        private InputStream inputStream;

        /**
         * 图像类型
         */
        private ImageExtension imageExtension;

        /**
         * 图像名称
         */
        private String filename;

        /**
         * 图像宽度
         */
        private Integer width;

        /**
         * 图像高度
         */
        private Integer height;

        /**
         * 图像段落对齐方式
         */
        private ParagraphAlignment alignment;

        /**
         * 图像上下边距
         */
        private Integer margin;

        /**
         * 图像前是否另起一页
         */
        private Boolean isPageBreak;

    }

    /**
     * Word表格构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WordTable {

        /**
         * 表格数据Map
         */
        private TableMap tableMap;

        /**
         * 表格行数
         */
        private Integer rowNum;

        /**
         * 表格列数
         */
        private Integer columnNum;

        /**
         * 表格宽度
         */
        private Integer width;

        /**
         * 表格对齐方式
         */
        private TableRowAlign alignment;

        /**
         * 表格前是否另起一页
         */
        private Boolean isPageBreak;

        /**
         * 表格Map类
         */
        public static class TableMap {

            @Getter
            private Map<Integer, List<String>> map;

            private AtomicInteger index;

            public TableMap() {
                map = new HashMap<>();
                index = new AtomicInteger(0);
            }

            /**
             * 增加
             *
             * @param rowContent 行内容
             */
            public void put(List<String> rowContent) {
                if (Objects.isNull(rowContent)) {
                    rowContent = new ArrayList<>();
                }
                map.put(index.getAndIncrement(), rowContent);
            }

            /**
             * 删除
             *
             * @param index 行索引
             * @return 返回最大行索引，若被删除行索引无效则返回-1
             */
            public int remove(Integer index) {
                if (Objects.isNull(map.remove(index))) {
                    return -1;
                }
                HashMap<Integer, List<String>> newMap = new HashMap<>();
                AtomicInteger newIndex = new AtomicInteger(0);
                for (List<String> value : map.values()) {
                    newMap.put(newIndex.getAndIncrement(), value);
                }
                this.map = newMap;
                this.index = newIndex;
                return this.index.intValue() - 1;
            }

            /**
             * 替换
             *
             * @param index      行索引
             * @param rowContent 行内容
             * @return 返回替换结果，若被替换行索引无效则返回false，其余情况返回true
             */
            public boolean replace(Integer index, List<String> rowContent) {
                if (Objects.isNull(rowContent)) {
                    rowContent = new ArrayList<>();
                }
                return Objects.nonNull(map.replace(index, rowContent));
            }

        }
    }
}