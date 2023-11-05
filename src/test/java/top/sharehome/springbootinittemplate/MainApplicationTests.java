package top.sharehome.springbootinittemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.oss.tencent.CosUtils;
import top.sharehome.springbootinittemplate.utils.redisson.CacheUtils;
import top.sharehome.springbootinittemplate.utils.redisson.RateLimitUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * 测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    UserService userService;

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
        if (ObjectUtils.isEmpty(userService.getOne(userLambdaQueryWrapper))) {
            userService.save(user);
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
        User admin = userService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, admin.getId())
                // 修改管理员账号
                .set(User::getAccount, "admin")
                // 修改管理员密码
                .set(User::getPassword, "123456");
        userService.update(userLambdaUpdateWrapper);
        admin = userService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
    }

    /**
     * 测试缓存工具类
     */
    @Test
    void testCacheUtils() {
        // 测试字符串
        CacheUtils.putString("test", "test");
        System.out.println(CacheUtils.getString("test"));
        System.out.println(CacheUtils.existsString("test"));
        CacheUtils.deleteString("test");

        // 测试数字
        CacheUtils.putNumber("test", 9999999999L);
        System.out.println(CacheUtils.getNumberDoubleValue("test"));
        System.out.println(CacheUtils.existsNumber("test"));
        CacheUtils.deleteNumber("test");

        // 测试List
        List<String> l = new ArrayList<String>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putList("test", l);
        System.out.println(CacheUtils.getList("test"));
        System.out.println(CacheUtils.existsList("test"));
        CacheUtils.deleteList("test");

        // 测试Set
        Set<String> s = new HashSet<String>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putSet("test", s);
        System.out.println(CacheUtils.getSet("test"));
        System.out.println(CacheUtils.existsSet("test"));
        CacheUtils.deleteSet("test");

        // 测试Map
        Map<String, String> m = new HashMap<String, String>() {
            {
                put("test1", "test1");
                put("test2", "test2");
            }
        };
        CacheUtils.putMap("test", m);
        System.out.println(CacheUtils.getMap("test"));
        System.out.println(CacheUtils.existsMap("test"));
        CacheUtils.deleteMap("test");
    }

    /**
     * 测试限流工具类
     */
    @Test
    void testRateLimitUtils() throws InterruptedException {
        try {
            for (int i = 0; i < 5; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
        ThreadUtils.sleep(Duration.ofSeconds(2));
        try {
            for (int i = 0; i < 10; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
    }

    /**
     * 测试腾讯云COS工具类——上传
     */
    @Test
    void testCosUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(CosUtils.upload(multipartFile, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testCosUtilsDelete() {
        CosUtils.delete("https://test-1306588126.cos.ap-chengdu.myqcloud.com/test/init/2023/11/03/5591bee0dcc14ad6b20d7fb8d78414f7_README.md");
    }

}