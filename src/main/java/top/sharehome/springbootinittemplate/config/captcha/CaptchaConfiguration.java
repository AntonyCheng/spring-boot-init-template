package top.sharehome.springbootinittemplate.config.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import top.sharehome.springbootinittemplate.config.captcha.condition.CaptchaCondition;

import java.awt.*;

/**
 * 验证码配置
 *
 * @author AntonyCheng
 */
@Configuration
@Conditional(CaptchaCondition.class)
@Slf4j
public class CaptchaConfiguration {

    /**
     * 验证码图片宽度
     */
    private static final int WIDTH = 160;

    /**
     * 验证码图片高度
     */
    private static final int HEIGHT = 60;

    /**
     * 验证码图片背景颜色
     */
    private static final Color BACKGROUND = Color.WHITE;

    /**
     * 验证码字体
     */
    private static final Font FONT = new Font("Arial", Font.BOLD, 48);

    /**
     * 圆圈干扰的字符验证码
     */
    @Lazy
    @Bean
    public CircleCaptcha circleCharCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 线段干扰的字符验证码
     */
    @Lazy
    @Bean
    public LineCaptcha lineCharCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 扭曲干扰的字符验证码
     */
    @Lazy
    @Bean
    public ShearCaptcha shearCharCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}