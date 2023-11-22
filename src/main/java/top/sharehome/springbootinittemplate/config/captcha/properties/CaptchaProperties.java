package top.sharehome.springbootinittemplate.config.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.captcha.enums.CaptchaCategory;
import top.sharehome.springbootinittemplate.config.captcha.enums.CaptchaType;

/**
 * 验证码配置
 *
 * @author xg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 验证码开关
     */
    private Boolean enabled;

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
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;

}