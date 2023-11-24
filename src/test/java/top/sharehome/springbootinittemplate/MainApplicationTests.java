package top.sharehome.springbootinittemplate;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.AuthService;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Map;

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

    @Resource
    private CaptchaService captchaService;

    @Test
    public void testCreateCaptcha() {
        Captcha captcha = captchaService.createCaptcha();
        System.out.println(captcha);
    }

    @Test
    public void testCheckCaptcha() {
//        captchaService.checkCaptcha();
    }

    public static void main(String[] args) {
        System.out.println("hello world");
    }

}