package top.sharehome.springbootinittemplate;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.pool2.PoolUtils;
import org.apache.http.util.NetUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;
import top.sharehome.springbootinittemplate.captcha.model.CaptchaCreate;
import top.sharehome.springbootinittemplate.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.AuthService;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
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
        if (ObjectUtils.isEmpty(authService.getOne(userLambdaQueryWrapper))) {
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

    public static void main(String[] args) {
//        String uuid1 = IdUtil.simpleUUID();
        String uuid2 = UUID.randomUUID().toString().replace("-", "");
        long l = System.currentTimeMillis();

        for (int i = 0; i < 10000000; i++) {
            uuid2 = UUID.randomUUID().toString().replace("-", "");
        }
//        for (int i = 0; i < 10000000; i++) {
//            uuid1 = IdUtil.simpleUUID();
//        }

        long l1 = System.currentTimeMillis();
        System.out.println(l1-l);
    }

}