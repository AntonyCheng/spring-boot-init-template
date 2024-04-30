package top.sharehome.springbootinittemplate.config.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.sharehome.springbootinittemplate.config.captcha.properties.enums.CaptchaCategory;
import top.sharehome.springbootinittemplate.config.captcha.properties.enums.CaptchaType;

/**
 * 验证码配置
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 验证码开关
     */
    private Boolean enable = false;

    /**
     * 验证码类型
     */
    private CaptchaType type = CaptchaType.CHAR;

    /**
     * 验证码类别
     */
    private CaptchaCategory category = CaptchaCategory.LINE;

    /**
     * 数字验证码位数
     */
    private Integer numberLength = 1;

    /**
     * 字符验证码长度
     */
    private Integer charLength = 4;

    /**
     * 验证码存活时间（单位：秒）
     */
    private Integer expired = 180;

}