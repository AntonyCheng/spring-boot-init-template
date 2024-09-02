package top.sharehome.springbootinittemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.AuthService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
@EnableAsync
@Slf4j
class MainApplicationTests {

    @Resource
    private AuthService authService;

    /**
     * 初始化管理员账号密码（数据库中默认内置有：admin/123456）
     */
    @Test
    void initAdmin() {
        User user = new User();
        // 设置用户账号
        user.setAccount("admin");
        // 设置用户密码
        user.setPassword("123456");
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getRole, "admin");
        if (!authService.exists(userLambdaQueryWrapper)) {
            authService.save(user);
            System.out.println("\n管理员身份创建成功！");
            // 创建之后切记前往数据库修改这条数据的"user_role"字段为"admin"
        } else {
            System.out.println("\n管理员身份已经存在！");
        }
    }

    /**
     * 修改管理员账号密码
     */
    @Test
    void updateAdmin() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getRole, "admin");
        User admin = authService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, admin.getId())
                // 修改管理员账号
                .set(User::getAccount, "admin")
                // 修改管理员密码
                .set(User::getPassword, "123456");
        authService.update(userLambdaUpdateWrapper);
        admin = authService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
    }

    /**
     * 初始化系统中文简体、中文繁体以及英文国际化键值对
     */
    @Test
    void initI18n() {
        generationI18nZhCN();
        System.out.println("=====================================================================");
        generationI18nZhTW();
        System.out.println("=====================================================================");
        generationI18nEnUS();
    }

    /**
     * 生成系统默认国际化键
     */
    static void generateI18nKey() {
        for (ReturnCode returnCode : ReturnCode.values()) {
            System.out.println(returnCode.name().toLowerCase().replace("_", "."));
        }
    }

    /**
     * 生成系统默认国际化中文值
     */
    static void generationI18nZhCNValue() {
        for (ReturnCode returnCode : ReturnCode.values()) {
            System.out.println(returnCode.getMsg());
        }
    }

    /**
     * 生成系统默认国际化中文简体键值对
     */
    static void generationI18nZhCN() {
        for (ReturnCode returnCode : ReturnCode.values()) {
            System.out.println(returnCode.name().toLowerCase().replace("_", ".") + "=" + returnCode.getMsg());
        }
    }

    /**
     * 生成系统默认国际化中文繁体键值对
     */
    static void generationI18nZhTW() {
        for (ReturnCode returnCode : ReturnCode.values()) {
            System.out.println(returnCode.name().toLowerCase().replace("_", ".") + "=" + ZhConverterUtil.toTraditional(returnCode.getMsg()));
        }
    }

    /**
     * 生成系统默认国际化英文键值对
     */
    static void generationI18nEnUS() {
        ReturnCode[] values = ReturnCode.values();
        List<String> list = Arrays.stream(values).map(returnCode -> Arrays.stream(returnCode.name().toLowerCase().split("_")).map(data -> {
            byte[] bytes = data.getBytes();
            bytes[0] = (byte) (bytes[0] - ('a' - 'A'));
            return new String(bytes);
        }).collect(Collectors.joining(" "))).collect(Collectors.toList());
        for (int i = 0; i < values.length; i++) {
            System.out.println(values[i].name().toLowerCase().replace("_", ".") + "=" + list.get(i));
        }
    }

    public static void main(String[] args) {
        System.out.println("\nHello World");
    }

}