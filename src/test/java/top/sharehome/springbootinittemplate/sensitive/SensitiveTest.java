package top.sharehome.springbootinittemplate.sensitive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.sensitive.DesensitizedUtils;
import top.sharehome.springbootinittemplate.utils.sensitive.SensitiveWordUtils;

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
    void testSensitiveWordUtils() {
        // 添加敏感词
        SensitiveWordUtils.addSensitiveDeny("你好", "我好", "大家好");
        // 准备待测试内容
        String text = "朋友们！大家好，我是AntonyCheng，你好我好！大家好！";
        // 判断内容中是否有敏感词
        Boolean hasSensitive = SensitiveWordUtils.hasSensitive(text);
        System.out.println(hasSensitive);
        // 获取内容中第一个敏感词
        String firstSensitive = SensitiveWordUtils.getFirstSensitive(text);
        System.out.println(firstSensitive);
        // 获取内容中所有的敏感词
        List<String> sensitiveList1 = SensitiveWordUtils.getSensitiveList(text);
        System.out.println(sensitiveList1);
        // 删除一个敏感词
        SensitiveWordUtils.removeSensitiveDeny("你好");
        List<String> sensitiveList2 = SensitiveWordUtils.getSensitiveList(text);
        System.out.println(sensitiveList2);
        // 添加一个白名单
        SensitiveWordUtils.addSensitiveAllow("大家好");
        List<String> sensitiveList3 = SensitiveWordUtils.getSensitiveList(text);
        System.out.println(sensitiveList3);
    }

    /**
     * 测试脱敏工具类
     */
    @Test
    void testDesensitizedUtils() {
        String str = "123!@#qweQWEasdASD";
        System.out.println("DesensitizedUtils.maskLeft(str, 0.5) = " + DesensitizedUtils.maskLeft(str, 0.5));
        System.out.println("DesensitizedUtils.maskMiddle(str, 0.5) = " + DesensitizedUtils.maskMiddle(str, 0.5));
        System.out.println("DesensitizedUtils.maskRight(str, 0.5) = " + DesensitizedUtils.maskRight(str, 0.5));
        System.out.println("DesensitizedUtils.all(str) = " + DesensitizedUtils.all(str));
        System.out.println("DesensitizedUtils.toEmpty(str) = " + DesensitizedUtils.toEmpty(str));
        System.out.println("DesensitizedUtils.toNull(str) = " + DesensitizedUtils.toNull(str));
        Long userId = 123456789123456789L;
        System.out.println("DesensitizedUtils.userId(userId) = " + DesensitizedUtils.userId(userId));
        String chineseName = "王二狗蛋";
        System.out.println("DesensitizedUtils.chineseName(chineseName) = " + DesensitizedUtils.chineseName(chineseName));
        String idCard = "110234200001019999";
        System.out.println("DesensitizedUtils.idCard(idCard) = " + DesensitizedUtils.idCard(idCard));
        String phone = "13188889999";
        System.out.println("DesensitizedUtils.phone(phone) = " + DesensitizedUtils.phone(phone));
        String address = "中国北京市海淀区翻斗花园一栋1单元";
        System.out.println("DesensitizedUtils.address(address) = " + DesensitizedUtils.address(address));
        String email = "1911261716@qq.com";
        System.out.println("DesensitizedUtils.email(email) = " + DesensitizedUtils.email(email));
        String password = "1234567890";
        System.out.println("DesensitizedUtils.password(password) = " + DesensitizedUtils.password(password));
        String licensePlate = "京A00001";
        System.out.println("DesensitizedUtils.licensePlate(licensePlate) = " + DesensitizedUtils.licensePlate(licensePlate));
        String bankCard = "1111222233334444555";
        System.out.println("DesensitizedUtils.bankCard(bankCard) = " + DesensitizedUtils.bankCard(bankCard));
        String ipv4 = "127.0.0.1";
        System.out.println("DesensitizedUtils.ipv4(ipv4) = " + DesensitizedUtils.ipv4(ipv4));
        String ipv6 = "0:0:0:0:0:0:0:1";
        System.out.println("DesensitizedUtils.ipv6(ipv6) = " + DesensitizedUtils.ipv6(ipv6));
        String mac = "00:00:00:00:00:00";
        System.out.println("DesensitizedUtils.mac(mac) = " + DesensitizedUtils.mac(mac));
    }

}
