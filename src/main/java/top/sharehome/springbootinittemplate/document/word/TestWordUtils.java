package top.sharehome.springbootinittemplate.document.word;

import cn.hutool.poi.word.WordUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.*;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 直接使用main函数对WordUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestWordUtils {

    private static final String READABLE_WORD_PATH_NAME = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/word/file/readable.docx";

    private static final String OUTPUT_ZIP_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/main/java/top/sharehome/springbootinittemplate/document/word/file/temp.zip";


    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(READABLE_WORD_PATH_NAME);
        File file = new File(OUTPUT_ZIP_FILE_PATH_NAME);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        WordUtils.getPictures(fileInputStream, fileOutputStream, PictureType.PNG);
        fileOutputStream.close();
    }
}
