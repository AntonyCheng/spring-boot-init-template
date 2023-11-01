package top.sharehome.springbootinittemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.redis.CacheUtils;
import top.sharehome.springbootinittemplate.utils.redis.RateLimitUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;

@SpringBootTest
@Slf4j
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
            log.info("\n管理员身份创建成功！");
            // 创建之后切记前往数据库修改这条数据的"user_role"字段为"admin"
        } else {
            log.info("\n管理员身份已经存在！");
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
        log.info("\n修改前的admin={}", admin);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, admin.getId())
                // 修改管理员账号
                .set(User::getAccount, "admin")
                // 修改管理员密码
                .set(User::getPassword, "123456");
        userService.update(userLambdaUpdateWrapper);
        admin = userService.getOne(userLambdaQueryWrapper);
        log.info("\n修改后的admin={}", admin);
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
        for (int i = 0; i < 5; i++) {
            try{
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }catch (CustomizeReturnException e){
                System.out.println("请求太多，请稍后");
            }
        }
        ThreadUtils.sleep(Duration.ofSeconds(2));
        for (int i = 0; i < 10; i++) {
            try{
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }catch (CustomizeReturnException e){
                System.out.println("请求太多，请稍后");
            }
        }
    }

}
