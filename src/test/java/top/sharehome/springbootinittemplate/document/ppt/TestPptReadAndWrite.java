package top.sharehome.springbootinittemplate.document.ppt;

import top.sharehome.springbootinittemplate.utils.document.ppt.PptUtils;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 直接使用main函数对PptUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestPptReadAndWrite {

    private static final String READABLE_PPT_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/ppt/file/readable.pptx";

    private static final String WRITABLE_PPT_FILE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/ppt/file/temp.pptx";

    public static void main(String[] args) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(WRITABLE_PPT_FILE_PATH_NAME)) {
            PptUtils.Writer writer = new PptUtils.Writer()
                    .addTextarea("Hello World, 你好世界！")
                    .addSlide()
                    .addTextarea("Hello World, 你好世界！");
            FileInputStream fileInputStream = new FileInputStream(READABLE_PPT_PATH_NAME);
            // 从READABLE_PPT_PATH_NAME文档中获取图片
            Map<String, List<byte[]>> imagesByteArray = new PptUtils.XMLReader(fileInputStream).getImagesByteArray();
            imagesByteArray.values().forEach(list -> {
                list.forEach(image -> {
                    // 将图片遍历插入WRITABLE_PPT_FILE_PATH_NAME文档中
                    writer.addSlide().addImage(new ByteArrayInputStream(image), new Rectangle2D.Double(20, 30, 100, 100));
                });
            });
            // 从READABLE_PPT_PATH_NAME文档中获取第一个表格
            List<PptUtils.PptTable.TableMap> tablesMaps = new PptUtils.XMLReader(new FileInputStream(READABLE_PPT_PATH_NAME)).getTablesMaps();
            PptUtils.PptTable.TableMap tableMap = tablesMaps.get(0);
            // 插入WRITABLE_PPT_FILE_PATH_NAME文档表格
            writer.addSlide().addTable(tableMap, new Rectangle2D.Double(40, 40, 0, 0), 90);
            // 执行最终写入
            writer.doWrite(outputStream);
        } catch (IOException e) {
            System.out.println("意外错误");
        }
    }

}
