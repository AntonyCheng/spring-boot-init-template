package top.sharehome.springbootinittemplate.sensitive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.sensitive.SensitiveUtils;

import java.util.List;

/**
 * 测试敏感词类
 *
 * @author AntonyCheng
 */
@SpringBootTest
@Slf4j
public class SensitiveTest {

    /**
     * 测试敏感词工具类
     */
    @Test
    void testSensitiveUtils() {
        // 添加敏感词
        SensitiveUtils.addSensitiveDeny("你好","我好","大家好");
        // 准备待测试内容
        String text = "朋友们！大家好，我是AntonyCheng，你好我好！大家好！";
        // 判断内容中是否有敏感词
        Boolean hasSensitive = SensitiveUtils.hasSensitive(text);
        System.out.println(hasSensitive);
        // 获取内容中第一个敏感词
        String firstSensitive = SensitiveUtils.getFirstSensitive(text);
        System.out.println(firstSensitive);
        // 获取内容中所有的敏感词
        List<String> sensitiveList1 = SensitiveUtils.getSensitiveList(text);
        System.out.println(sensitiveList1);
        // 删除一个敏感词
        SensitiveUtils.removeSensitiveDeny("你好");
        List<String> sensitiveList2 = SensitiveUtils.getSensitiveList(text);
        System.out.println(sensitiveList2);
        // 添加一个白名单
        SensitiveUtils.addSensitiveAllow("大家好");
        List<String> sensitiveList3 = SensitiveUtils.getSensitiveList(text);
        System.out.println(sensitiveList3);
    }

}
