package top.sharehome.springbootinittemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.redisson.LockUtils;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private UserService userService;

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

    @Test
    void testLockUtils() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LockUtils.lock("test", 0, 3000, () -> {
                    try {
                        System.out.println("tok");
                        ThreadUtils.sleep(Duration.ofSeconds(1));
                        return "tok";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    System.out.println("tfail");
                    return "tfail";
                });
            }
        }).start();
        LockUtils.lock("test", 0, 3000, () -> {
            try {
                System.out.println("ok");
                ThreadUtils.sleep(Duration.ofSeconds(10));
                return "ok";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            System.out.println("fail");
            return "fail";
        });
    }


    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(123L);
        user1.setName("123");

        User user2 = new User();
        user2.setId(123L).setName("123");

        System.out.println(user1);
        System.out.println(user2);
    }

}