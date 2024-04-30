package top.sharehome.springbootinittemplate.utils.document.word;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.deepoove.poi.XWPFTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeExcelException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
         * 导出Word.docx模板目录下的模板文件到响应流，模板目录一定是resources文件夹下templates/word目录
         *
         * @param templateName 模板名称
         * @param tagMap       标签Map
         * @param filename     导出文件名称
         * @param response     响应流
         */
        public static void export(String templateName, Map<String, Object> tagMap, String filename, HttpServletResponse response) {
            try {
                handleWordResponse(filename, response);
                ServletOutputStream outputStream = response.getOutputStream();
                export(templateName, tagMap, outputStream);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 导出Word.docx模板目录下的模板文件到输出流，模板目录一定是resources文件夹下templates/word目录
         *
         * @param templateName 模板名称
         * @param tagMap       标签Map
         * @param outputStream 输出流
         */
        public static void export(String templateName, Map<String, Object> tagMap, OutputStream outputStream) {
            try {
                ClassPathResource classPathResource = new ClassPathResource("templates/word/" + templateName + ".docx");
                if (!classPathResource.exists()) {
                    throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "模板文件[" + templateName + ".docx]未找到");
                }
                InputStream inputStream = classPathResource.getInputStream();
                XWPFTemplate template = XWPFTemplate.compile(inputStream).render(tagMap);
                template.writeAndClose(outputStream);
                inputStream.close();
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 处理ContentType是Word格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private static void handleWordResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName;
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

    }

    /**
     * 写入Word数据内部类
     */
    public static class Writer {

        /**
         * 创建文档
         *
         * @return 返回一个初始化文档
         */
        public static XWPFDocument createWord() {
            return new XWPFDocument();
        }

        /**
         * 添加段落
         *
         * @param document 文档本体
         * @param text     段落内容，为空即为""
         */
        public static void addParagraph(XWPFDocument document, String text) {
            addParagraph(document, text, null, null, null, null, null, null, null, null, null);
        }

        /**
         * 添加段落
         *
         * @param document         文档本体
         * @param text             段落内容，为空即为""
         * @param paragraphDetails 段落细节
         */
        public static void addParagraph(XWPFDocument document, String text, ParagraphDetails paragraphDetails) {
            addParagraph(
                    document,
                    text,
                    paragraphDetails.getFontSize(),
                    paragraphDetails.getFontFamily(),
                    paragraphDetails.getColor(),
                    paragraphDetails.getIsItalic(),
                    paragraphDetails.getIsBold(),
                    paragraphDetails.getUnderlineType(),
                    paragraphDetails.getAlignment(),
                    paragraphDetails.getSpacingBetween(),
                    paragraphDetails.getIsPageBreak()
            );
        }

        /**
         * 添加图片
         *
         * @param document    文档本体
         * @param inputStream 图片输入流
         */
        public static void addPicture(XWPFDocument document, InputStream inputStream) {
            addPicture(document, inputStream, null, null, null, null, null, null);
        }

        /**
         * 添加图片
         *
         * @param document       文档本体
         * @param inputStream    图片输入流
         * @param pictureDetails 图片细节
         */
        public static void addPicture(XWPFDocument document, InputStream inputStream, PictureDetails pictureDetails) {
            addPicture(
                    document,
                    inputStream,
                    pictureDetails.getPictureType(),
                    pictureDetails.getFilename(),
                    pictureDetails.getWidth(),
                    pictureDetails.getHeight(),
                    pictureDetails.getAlignment(),
                    pictureDetails.getSpacingBetween()
            );
        }

        /**
         * 添加表格
         *
         * @param document     文档本体
         * @param tableContent 表格内容
         */
        public static void addTable(XWPFDocument document, TableMap tableContent) {
            addTable(document, tableContent, null, null, null, null);
        }

        /**
         * 添加表格
         *
         * @param document     文档本体
         * @param tableContent 表格内容
         * @param tableDetails 表格细节
         */
        public static void addTable(XWPFDocument document, TableMap tableContent, TableDetails tableDetails) {
            addTable(
                    document,
                    tableContent,
                    tableDetails.getRowNum(),
                    tableDetails.getColumnNum(),
                    tableDetails.getWidth(),
                    tableDetails.getAlignment()
            );
        }

        /**
         * 添加新的一页
         *
         * @param document 文档本体
         */
        public static void addBreakPage(XWPFDocument document) {
            // 如果连文档都没有，那就直接返回即可
            if (Objects.isNull(document)) {
                return;
            }
            document.createParagraph().createRun().addBreak(BreakType.PAGE);
        }

        /**
         * 添加段落
         *
         * @param document       文档本体
         * @param text           段落内容，为空即为""
         * @param fontSize       字体大小，为空或者小于等于零即为12（小四）
         * @param fontFamily     字体名称，为空即为"Arial"（等线）
         * @param color          文字颜色，为空即为"000000"（黑色）
         * @param isItalic       是否斜体，为空即为false
         * @param isBold         是否加粗，为空即为false
         * @param underlineType  下划线类型，为空即为UnderlinePatterns.NONE
         * @param alignment      段落对其方式，为空即为ParagraphAlignment.BOTH
         * @param spacingBetween 行间距，为空或小于等于零即为1
         * @param isPageBreak    是否另起一页，为空即为false
         */
        private static void addParagraph(XWPFDocument document, String text, Integer fontSize, String fontFamily, String color, Boolean isItalic, Boolean isBold, UnderlinePatterns underlineType, ParagraphAlignment alignment, Integer spacingBetween, Boolean isPageBreak) {
            // 如果连文档都没有，那就直接返回即可
            if (Objects.isNull(document)) {
                return;
            }
            XWPFParagraph paragraph = document.createParagraph();
            // 创建段落
            XWPFRun run = paragraph.createRun();
            // 设置段落文本
            run.setText(StringUtils.isEmpty(text) ? "" : text);
            // 设置段落字体大小
            int size = Objects.isNull(fontSize) || fontSize <= 0 ? 12 : fontSize;
            run.setFontSize(size);
            // 设置段落字体
            run.setFontFamily(StringUtils.isEmpty(fontFamily) ? "Arial" : color);
            // 设置段落颜色
            run.setColor(StringUtils.isEmpty(color) ? "000000" : color);
            // 设置段落是否斜体
            run.setItalic(Objects.isNull(isItalic) ? Boolean.FALSE : isItalic);
            // 设置段落是否粗体
            run.setBold(Objects.isNull(isBold) ? Boolean.FALSE : isBold);
            // 设置段落下划线
            run.setUnderline(Objects.isNull(underlineType) ? UnderlinePatterns.NONE : underlineType);
            // 设置段落对齐方式
            paragraph.setAlignment(Objects.isNull(alignment) ? ParagraphAlignment.BOTH : alignment);
            // 设置段落行间距
            paragraph.setSpacingBetween(Objects.isNull(spacingBetween) || spacingBetween <= 0 ? 1 : spacingBetween, LineSpacingRule.AUTO);
            // 设置段落首行缩进
            paragraph.setIndentationFirstLine(size * 40);
            // 设置段落段前是否另起一页
            paragraph.setPageBreak(Objects.isNull(isPageBreak) ? Boolean.FALSE : isPageBreak);
        }

        /**
         * 添加图片
         *
         * @param document       文档本体
         * @param inputStream    图片输入流
         * @param pictureType    图片类型，为空即为PictureType.PNG
         * @param filename       图片名称，为空即为"filename"
         * @param width          图片宽度，为空即为图片原宽度
         * @param height         图片高度，为空即为图片原高度
         * @param alignment      图片段落对齐方式，为空即为ParagraphAlignment.BOTH
         * @param spacingBetween 图片段落行间距，为空即为1
         */
        private static void addPicture(XWPFDocument document, InputStream inputStream, PictureType pictureType, String filename, Integer width, Integer height, ParagraphAlignment alignment, Integer spacingBetween) {
            try {
                // 如果连数据流或者文档都没有，那就直接返回即可
                if (Objects.isNull(document) || Objects.isNull(inputStream)) {
                    return;
                }
                XWPFParagraph paragraph = document.createParagraph();
                // 添加图片的前提就是存在一个段落
                XWPFRun run = paragraph.createRun();
                // 先获取图片信息
                int imgHeight = ImageIO.read(inputStream).getHeight();
                inputStream.reset();
                int imgWidth = ImageIO.read(inputStream).getWidth();
                inputStream.reset();
                // 判空
                pictureType = Objects.isNull(pictureType) ? PictureType.PNG : pictureType;
                filename = StringUtils.isEmpty(filename) ? "picture" : filename;
                width = Objects.isNull(width) ? imgWidth : width;
                height = Objects.isNull(height) ? imgHeight : height;
                // 添加图片
                run.addPicture(inputStream, pictureType, filename, Units.toEMU(width), Units.toEMU(height));
                // 设置图片段落格式
                paragraph.setAlignment(Objects.isNull(alignment) ? ParagraphAlignment.BOTH : alignment);
                // 设置图片段落行间距
                paragraph.setSpacingBetween(Objects.isNull(spacingBetween) || spacingBetween <= 0 ? 1 : spacingBetween, LineSpacingRule.AUTO);
            } catch (IOException | InvalidFormatException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 添加表格
         *
         * @param document     文档本体
         * @param tableContent 表格内容
         * @param rowNum       行数，为空或者数值小于表格本身行值即为表格本身行值
         * @param columnNum    列数，为空或者数值小于表格本身列值即为表格本身列值
         * @param width        表格宽度，为空即为8000
         * @param alignment    表格对齐方式，为空即为TableRowAlign.CENTER
         */
        private static void addTable(XWPFDocument document, TableMap tableContent, Integer rowNum, Integer columnNum, Integer width, TableRowAlign alignment) {
            // 如果连文档都没有，或者输入预设行列值异常，那就直接返回即可
            if (Objects.isNull(document) || (Objects.nonNull(rowNum) && rowNum <= 0) || (Objects.nonNull(columnNum) && columnNum <= 0)) {
                return;
            }
            // 由表格内容获取表格本身的行列值
            Map<Integer, List<String>> map = tableContent.getMap();
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
                return;
            } else {
                // 先把行值从索引值改为真实数值
                maxRow++;
                // 如果表格本身有行值，那就和预设值作比较，判断预设值是否是无效值
                if (Objects.nonNull(rowNum) && maxRow < rowNum) {
                    maxRow = rowNum;
                }
                if (Objects.nonNull(columnNum) && maxColumn < columnNum) {
                    maxColumn = columnNum;
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
            table.setWidth(Objects.isNull(width) ? 8000 : width);
            table.setTableAlignment(Objects.isNull(alignment) ? TableRowAlign.CENTER : alignment);
        }

        /**
         * 将Word写入响应流
         *
         * @param document 文档本体
         * @param fileName 响应文件名
         * @param response 响应流
         */
        public static void doWrite(XWPFDocument document, String fileName, HttpServletResponse response) {
            try {
                handleWordResponse(fileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                doWrite(document, outputStream);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 将Word写入输出流
         *
         * @param document     文档本体
         * @param outputStream 输出流
         */
        public static void doWrite(XWPFDocument document, OutputStream outputStream) {
            try (outputStream) {
                document.write(outputStream);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 处理ContentType是Word格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private static void handleWordResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
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

    }

    /**
     * 读取Word数据内部类
     */
    public static class Reader {

        /**
         * 从Word数据流中获取段落数据，转变为TXT格式，输出到响应流中
         *
         * @param fileName    文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getTxtParagraphs(String fileName, InputStream inputStream, HttpServletResponse response) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                handleTxtResponse(fileName, response);
                List<byte[]> paragraphs = getParagraphs(inputStream);
                paragraphs.forEach(paragraph -> {
                    try {
                        outputStream.write(paragraph);
                        outputStream.write('\n');
                    } catch (IOException e) {
                        throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
                    }
                });
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取图片数据，转变成PNG格式，压缩后输出到响应流中
         *
         * @param zipFileName 压缩后的文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getPngPictures(String zipFileName, InputStream inputStream, HttpServletResponse response) {
            try {
                handleZipResponse(zipFileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                getPictures(inputStream, outputStream, PictureType.PNG);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取图片数据，转变成JPEG/JPG格式，压缩后输出到响应流中
         *
         * @param zipFileName 压缩后的文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getJpegPictures(String zipFileName, InputStream inputStream, HttpServletResponse response) {
            try {
                handleZipResponse(zipFileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                getPictures(inputStream, outputStream, PictureType.JPEG);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取图片数据，压缩后输出到一个输出流中
         *
         * @param inputStream  Word输入流
         * @param outputStream 输出流
         * @param pictureType  图片格式
         */
        public static void getPictures(InputStream inputStream, OutputStream outputStream, PictureType pictureType) {
            getPictures(inputStream, outputStream, 5, pictureType);
        }

        /**
         * 从Word数据流中获取图片数据，压缩后输出到一个输出流中
         *
         * @param inputStream  Word输入流
         * @param outputStream 输出流
         * @param zipLevel     压缩等级1-9，等级越高，压缩效率越高
         * @param pictureType  图片格式
         */
        public static void getPictures(InputStream inputStream, OutputStream outputStream, Integer zipLevel, PictureType pictureType) {
            try {
                List<byte[]> pictures = getPictures(inputStream);
                ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream);
                zipArchiveOutputStream.setLevel(zipLevel);
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                for (byte[] picture : pictures) {
                    String entryName = uuid + "_" + index + pictureType.getExtension();
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    zipArchiveOutputStream.putArchiveEntry(entry);
                    ByteBuf buf = Unpooled.copiedBuffer(picture);
                    buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                    index++;
                }
                zipArchiveOutputStream.finish();
                zipArchiveOutputStream.close();
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格数据，转变成XLSX格式，压缩后输出到响应流中
         *
         * @param zipFileName 压缩后的文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getXlsxTables(String zipFileName, InputStream inputStream, HttpServletResponse response) {
            try {
                handleZipResponse(zipFileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                getTables(inputStream, outputStream, ExcelTypeEnum.XLSX);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格数据，转变成XLS格式，压缩后输出到响应流中
         *
         * @param zipFileName 压缩后的文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getXlsTables(String zipFileName, InputStream inputStream, HttpServletResponse response) {
            try {
                handleZipResponse(zipFileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                getTables(inputStream, outputStream, ExcelTypeEnum.XLS);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格数据，转变成CSV格式，压缩后输出到响应流中
         *
         * @param zipFileName 压缩后的文件名
         * @param inputStream Word输入流
         * @param response    响应流
         */
        public static void getCsvTables(String zipFileName, InputStream inputStream, HttpServletResponse response) {
            try {
                handleZipResponse(zipFileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                getTables(inputStream, outputStream, ExcelTypeEnum.CSV);
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格数据，压缩后输出到一个输出流中
         *
         * @param inputStream  Word输入流
         * @param outputStream 输出流
         * @param excelType    表格格式
         */
        public static void getTables(InputStream inputStream, OutputStream outputStream, ExcelTypeEnum excelType) {
            getTables(inputStream, outputStream, 5, excelType);
        }

        /**
         * 从Word数据流中获取表格数据，压缩后输出到一个输出流中
         *
         * @param inputStream  Word输入流
         * @param outputStream 输出流
         * @param zipLevel     压缩等级1-9，等级越高，压缩效率越高
         * @param excelType    表格格式
         */
        public static void getTables(InputStream inputStream, OutputStream outputStream, Integer zipLevel, ExcelTypeEnum excelType) {
            try {
                List<byte[]> tables = getTables(inputStream, excelType);
                ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream);
                zipArchiveOutputStream.setLevel(zipLevel);
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                for (byte[] picture : tables) {
                    String entryName = uuid + "_" + index + excelType.getValue();
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    zipArchiveOutputStream.putArchiveEntry(entry);
                    ByteBuf buf = Unpooled.copiedBuffer(picture);
                    buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                    index++;
                }
                zipArchiveOutputStream.finish();
                zipArchiveOutputStream.close();
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取段落文本数据
         *
         * @param inputStream Word输入流
         * @return 返回Word中所有段落数据组成的Map，key为段落序号，value为段落文本
         */
        public static Map<Integer, String> getParagraphsText(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                List<XWPFParagraph> paragraphs = doc.getParagraphs();
                Map<Integer, String> paragraphMap = new HashMap<>();
                int index = 0;
                for (XWPFParagraph paragraph : paragraphs) {
                    String text = paragraph.getText();
                    if (StringUtils.isNotEmpty(text)) {
                        paragraphMap.put(index, text);
                        index++;
                    }
                }
                return paragraphMap;
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取图片数据
         *
         * @param inputStream Word输入流
         * @return 返回Word中所有图片数据组成的Map，key为图片序号，value为图片Byte数组
         */
        public static Map<Integer, byte[]> getPicturesArray(InputStream inputStream) {
            List<byte[]> pictures = getPictures(inputStream);
            Map<Integer, byte[]> picturesMap = new HashMap<>();
            int index = 0;
            for (byte[] picture : pictures) {
                picturesMap.put(index, picture);
                index++;
            }
            return picturesMap;
        }

        /**
         * 从Word数据流中获取表格文本数据
         *
         * @param inputStream Word输入流
         * @return 返回Word中所有表格数据，每个表格均封装成Map，key值为行号，从0开始，value为表格封装对象
         */
        public static Map<Integer, TableMap> getTablesText(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                List<XWPFTable> tables = doc.getTables();
                List<TableMap> tableMaps = tables.stream().map(table -> {
                    TableMap tableMap = new TableMap();
                    int numberOfRows = table.getNumberOfRows();
                    for (int i = 0; i < numberOfRows; i++) {
                        List<String> cellList = table.getRow(i).getTableCells().stream().map(XWPFTableCell::getText).collect(Collectors.toList());
                        tableMap.put(cellList);
                    }
                    return tableMap;
                }).toList();
                HashMap<Integer, TableMap> res = new HashMap<>();
                for (int i = 0; i < tableMaps.size(); i++) {
                    res.put(i, tableMaps.get(i));
                }
                return res;
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取段落Byte[]
         *
         * @param inputStream Word数据流
         * @return 返回段落Byte[]集合
         */
        private static List<byte[]> getParagraphs(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                return doc.getParagraphs()
                        .stream()
                        .map(paragraph -> paragraph.getText().getBytes())
                        .filter(ObjectUtils::isNotEmpty)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取图片Byte[]
         *
         * @param inputStream Word数据流
         * @return 返回图片Byte[]集合
         */
        private static List<byte[]> getPictures(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                return doc.getAllPictures()
                        .stream()
                        .map(XWPFPictureData::getData)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格，封装成xlsx格式文件Byte[]
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param inputStream Word输入流
         * @return 返回XLSX文件byte[]集合
         */
        private static List<byte[]> getTables(InputStream inputStream, ExcelTypeEnum excelType) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                List<XWPFTable> tables = doc.getTables();
                return tables.stream().map(table -> {
                    List<List<String>> tableList = new ArrayList<>();
                    int numberOfRows = table.getNumberOfRows();
                    for (int i = 0; i < numberOfRows; i++) {
                        List<String> cellList = table.getRow(i).getTableCells().stream().map(XWPFTableCell::getText).collect(Collectors.toList());
                        tableList.add(cellList);
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                    return byteArrayOutputStream.toByteArray();
                }).collect(Collectors.toList());
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 处理ContentType是Txt格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private static void handleTxtResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
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
        private static void handleZipResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
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
            response.setContentType("application/x-zip-compressed;charset=UTF-8");
        }

    }

    /**
     * 段落细节构造类
     */
    @Data
    @Builder(setterPrefix = "set")
    public static class ParagraphDetails {

        /**
         * 字体大小，为空或者小于等于零即为12（小四）
         */
        private Integer fontSize;

        /**
         * 字体名称，为空即为"Arial"（等线）
         */
        private String fontFamily;

        /**
         * 文字颜色，为空即为"000000"（黑色）
         */
        private String color;

        /**
         * 是否斜体，为空即为false
         */
        private Boolean isItalic;

        /**
         * 是否加粗，为空即为false
         */
        private Boolean isBold;

        /**
         * 下划线类型，为空即为UnderlinePatterns.NONE
         */
        private UnderlinePatterns underlineType;

        /**
         * 段落对其方式，为空即为ParagraphAlignment.BOTH
         */
        private ParagraphAlignment alignment;

        /**
         * 行间距，为空或小于等于零即为1
         */
        private Integer spacingBetween;

        /**
         * 是否另起一页，为空即为false
         */
        private Boolean isPageBreak;

    }

    /**
     * 图片细节构造类
     */
    @Data
    @Builder(setterPrefix = "set")
    public static class PictureDetails {

        /**
         * 图片类型
         */
        PictureType pictureType;

        /**
         * 图片名称
         */
        String filename;

        /**
         * 图片宽度
         */
        Integer width;

        /**
         * 图片高度
         */
        Integer height;

        /**
         * 图片段落对齐方式
         */
        ParagraphAlignment alignment;

        /**
         * 图片段落行间距
         */
        Integer spacingBetween;

    }

    /**
     * 表格细节构造类
     */
    @Data
    @Builder(setterPrefix = "set")
    public static class TableDetails {

        /**
         * 行数
         */
        private Integer rowNum;

        /**
         * 列数
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

    }

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
