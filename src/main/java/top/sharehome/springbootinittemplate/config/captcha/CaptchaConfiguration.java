package top.sharehome.springbootinittemplate.config.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import top.sharehome.springbootinittemplate.config.captcha.generator.UnsignedMathGenerator;

import java.awt.*;

/**
 * 验证码配置
 *
 * @author AntonyCheng
 */
@Configuration
public class CaptchaConfiguration {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 60;
    private static final Color BACKGROUND = Color.WHITE;
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
     * 圆圈干扰的算术验证码
     */
    @Lazy
    @Bean
    public CircleCaptcha circleMathCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
        captcha.setGenerator(new UnsignedMathGenerator());
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 线条干扰的算术验证码
     */
    @Lazy
    @Bean
    public LineCaptcha lineMathCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
        captcha.setGenerator(new UnsignedMathGenerator());
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 圆圈干扰的算术验证码
     */
    @Lazy
    @Bean
    public ShearCaptcha shearMathCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
        captcha.setGenerator(new UnsignedMathGenerator());
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

}