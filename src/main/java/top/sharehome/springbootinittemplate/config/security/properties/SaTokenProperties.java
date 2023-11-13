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
     * 是否开启鉴权
     */
    private boolean enableAuthorization;

    /**
     * 是否开启认证
     */
    private boolean enableIdentification;

    /**
     * 是否使用jwt
     */
    private boolean enableJwt;

}