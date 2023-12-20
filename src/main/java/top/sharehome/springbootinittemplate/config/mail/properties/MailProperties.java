package top.sharehome.springbootinittemplate.config.mail.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    /**
     * 是否开启邮件功能
     */
    private Boolean enable = false;

}