package top.sharehome.springbootinittemplate.config.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SaToken配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "sa-token")
public class SaTokenProperties {

    /**
     * 是否启用SaToken认证鉴权功能
     */
    private Boolean enableSa = true;

    /**
     * 是否使用JWT
     */
    private Boolean enableJwt = false;

}