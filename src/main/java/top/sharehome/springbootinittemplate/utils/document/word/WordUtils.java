package top.sharehome.springbootinittemplate.utils.document.word;

import cn.hutool.poi.word.WordUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
     * 从Word数据流中获取数据，转变成PNG格式，压缩后输出到响应流中
     *
     * @param zipFileName 压缩后的文件名
     * @param inputStream 输入流
     * @param response    响应流
     */
    public static void getPngPictures(String zipFileName, InputStream inputStream, HttpServletResponse response) {
        try {
            handleZipResponse(zipFileName, response);
            ServletOutputStream outputStream = response.getOutputStream();
            getPictures(inputStream, outputStream, 5, PictureType.PNG);
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
        }
    }

    /**
     * 从Word数据流中获取数据，转变成JPEG/JPG格式，压缩后输出到响应流中
     *
     * @param zipFileName 压缩后的文件名
     * @param inputStream 输入流
     * @param response    响应流
     */
    public static void getJpegPictures(String zipFileName, InputStream inputStream, HttpServletResponse response) {
        try {
            handleZipResponse(zipFileName, response);
            ServletOutputStream outputStream = response.getOutputStream();
            getPictures(inputStream, outputStream, 5, PictureType.JPEG);
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.WORD_FILE_ERROR);
        }
    }

    /**
     * 从Word数据流中获取数据，压缩后输出到一个输出流中
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @param pictureType  图片格式
     */
    public static void getPictures(InputStream inputStream, OutputStream outputStream, PictureType pictureType) {
        getPictures(inputStream, outputStream, 5, pictureType);
    }

    /**
     * 从Word数据流中获取数据，压缩后输出到一个输出流中
     *
     * @param inputStream  输入流
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
     * 从Word数据流中获取图片Byte[]，同时关闭流
     *
     * @param inputStream Word数据流
     * @return 返回图片Byte[]集合
     */
    private static List<byte[]> getPictures(InputStream inputStream) throws IOException {
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(inputStream);
            return doc.getAllPictures().stream().map(XWPFPictureData::getData).collect(Collectors.toList());
        } finally {
            if (Objects.nonNull(doc)) {
                doc.close();
            }
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
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
        }else {
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
     * 处理ContentType是Zip格式的响应
     *
     * @param fileName 文件名
     * @param response 响应
     */
    private static void handleZipResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        String realName = null;
        if (StringUtils.isBlank(fileName)) {
            realName = UUID.randomUUID().toString().replace("-", "") + ".zip";
        }else {
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
