package top.sharehome.springbootinittemplate.utils.document.word;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.*;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
     * 从Word数据流中获取段落数据，转变为TXT格式，输出到响应流中
     *
     * @param fileName    文件名
     * @param inputStream Word输入流
     * @param response    响应流
     */
    public static void getTxtParagraphs(String fileName, InputStream inputStream, HttpServletResponse response) {
        try (ServletOutputStream outputStream = response.getOutputStream()){
            handleTxtResponse(fileName, response);
            List<byte[]> paragraphs = getParagraphs(inputStream);
            paragraphs.forEach(paragraph->{
                try {
                    outputStream.write(paragraph);
                    outputStream.write('\n');
                } catch (IOException e) {
                    throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
                }
            });
        }catch (IOException e){
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
