package top.sharehome.springbootinittemplate.config.encrypt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 请求参数加密配置
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "encrypt")
public class EncryptProperties {

    /**
     * 请求参数加密开关
     */
    private Boolean enable = false;

}