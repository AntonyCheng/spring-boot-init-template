package top.sharehome.springbootinittemplate.document.ppt;

import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.utils.document.ppt.PptUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 直接使用main函数对PptUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPptReadAndWrite {

    public static void main(String[] args) {
        String tempFile = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/ppt/file/temp.pptx";
        try {
            PptUtils.Writer writer = new PptUtils.Writer();
            writer.addSlide()
                    .addTextarea("AntonyCheng12345678910qwertyuiop!@#$%^&*()一二三四五六七八九十")
                    .addSlide()
                    .addTextarea(StringUtils.reverse("AntonyCheng12345678910qwertyuiop!@#$%^&*()一二三四五六七八九十"))
                    .doWrite(new FileOutputStream(tempFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
