package top.sharehome.springbootinittemplate.document.ppt;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.PictureData;
import top.sharehome.springbootinittemplate.utils.document.ppt.PptUtils;

import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 直接使用main函数对PptUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPptReadAndWrite {

    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("D:\\Desktop\\模型头像图片\\安全帽检测算法.jpg");
        String tempFile = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/ppt/file/temp.pptx";
        try {
            PptUtils.Writer writer = new PptUtils.Writer();
            PptUtils.PptTable.TableMap tableMap = new PptUtils.PptTable.TableMap();
            tableMap.put(List.of("123", "234", "345"));
            tableMap.put(List.of("123", "345"));
            tableMap.put(List.of("123"));
            writer
                    .addImage(fileInputStream)
                    .addSlide()
                    .addTable(tableMap, 80, 7, 7)
                    .addSlide()
                    .addTextarea(StringUtils.reverse("AntonyCheng12345678910qwertyuiop!@#$%^&*()一二三四五六七八九十"))
                    .doWrite(new FileOutputStream(tempFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
