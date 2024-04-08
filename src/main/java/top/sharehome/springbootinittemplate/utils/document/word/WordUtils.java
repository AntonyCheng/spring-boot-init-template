package top.sharehome.springbootinittemplate.utils.document.word;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
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
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
            addPicture(document, inputStream, null, null, null, null);
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
                    pictureDetails.getHeight()
            );
        }

        /**
         * 添加表格
         *
         * @param document     文档本体
         * @param rowNumber    行数
         * @param columnNum    列数
         * @param tableContent 表格内容
         */
        public static void addTable(XWPFDocument document, Integer rowNumber, Integer columnNum, TableMap tableContent) {
            // 如果连文档都没有，那就直接返回即可
            if (Objects.isNull(document) || rowNumber <= 0 || columnNum <= 0) {
                return;
            }
            // 判断表格内容是否超出表格预设值
            Map<Integer, List<String>> map = tableContent.getMap();
            map.forEach((k, v) -> {
                if (k >= rowNumber) {
                    throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR, "表格内容[行]超出预设值");
                }
                if (v.size() > columnNum) {
                    throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR, "表格内容[列]超出预设值");
                }
            });
            // 开始填充表格
            XWPFTable table = document.createTable(rowNumber, columnNum);
            map.forEach((k,v)->{
                for (int i = 0; i < v.size(); i++) {
                    table.getRow(k).getCell(i).setText(v.get(i));
                }
            });
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
         * @param document    文档本体
         * @param inputStream 图片输入流
         * @param pictureType 图片类型
         * @param filename    图片名称
         * @param width       图片宽度
         * @param height      图片高度
         */
        private static void addPicture(XWPFDocument document, InputStream inputStream, PictureType pictureType, String filename, Integer width, Integer height) {
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
            } catch (IOException | InvalidFormatException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
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
                realName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName + ".docx";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8.toString())
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
         * @return 返回Word中所有表格数据，每个表格均封装成Map，key值为行号，从0开始，value为该行每个单元格内容，最后所有Map装进List中返回
         */
        public static List<Map<Integer, List<String>>> getTablesText(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                List<XWPFTable> tables = doc.getTables();
                return tables.stream().map(table -> {
                    Map<Integer, List<String>> tableMap = new HashMap<>();
                    int numberOfRows = table.getNumberOfRows();
                    for (int i = 0; i < numberOfRows; i++) {
                        List<String> cellList = table.getRow(i).getTableCells().stream().map(XWPFTableCell::getText).collect(Collectors.toList());
                        tableMap.put(i, cellList);
                    }
                    return tableMap;
                }).collect(Collectors.toList());
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取段落Byte[]
         *
         * @param inputStream Word数据流
         * @return 返回图片Byte[]集合
         */
        private static List<byte[]> getParagraphs(InputStream inputStream) {
            try (XWPFDocument doc = new XWPFDocument(inputStream); inputStream) {
                return doc.getParagraphs().stream()
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
                return doc.getAllPictures().stream().map(XWPFPictureData::getData).collect(Collectors.toList());
            } catch (IOException e) {
                throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
            }
        }

        /**
         * 从Word数据流中获取表格，封装成xlsx格式文件Byte[]
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param inputStream Word输入流
         * @return 返回Word中所有表格数据，每个表格均封装成Map，key值为行号，从0开始，value为该行每个单元格内容，最后所有Map装进List中返回
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
                realName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName + ".txt";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8.toString())
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
                realName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName + ".zip";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8.toString())
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

    }

    /**
     * 表格Map类
     */
    public static class TableMap {

        @Getter
        private final Map<Integer, List<String>> map;

        private final AtomicInteger index;

        public TableMap() {
            map = new HashMap<>();
            index = new AtomicInteger(0);
        }

        public void put(List<String> rowContent) {
            map.put(index.getAndIncrement(), rowContent);
        }

    }

}
