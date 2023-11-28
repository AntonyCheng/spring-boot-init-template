package top.sharehome.springbootinittemplate.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.captcha.properties.enums.CaptchaCategory;
import top.sharehome.springbootinittemplate.captcha.properties.enums.CaptchaType;

/**
 * 验证码配置
 *
 * @author AntonyCheng
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "captcha")
@Conditional(CaptchaCondition.class)
public class CaptchaProperties {

    /**
     * 验证码开关
     */
    private boolean enable;

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证码类别
     */
    private CaptchaCategory category;

    /**
     * 数字验证码位数
     */
    private int numberLength;

    /**
     * 字符验证码长度
     */
    private int charLength;

    /**
     * 验证码存活时间（单位：秒）
     */
    private int expired;

}