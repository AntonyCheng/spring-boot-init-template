package top.sharehome.springbootinittemplate.config.oss.service.tencent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 腾讯云COS配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "oss.tencent")
public class TencentProperties {

    /**
     * 是否开启
     */
    private Boolean enable = false;

    /**
     * 地域
     */
    private String region;

    /**
     * 密钥ID
     */
    private String secretId;

    /**
     * 密钥KEY
     */
    private String secretKey;

    /**
     * 桶名称
     */
    private String bucketName;

}