package top.sharehome.springbootinittemplate.config.oss.service.ali.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云OSS配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "oss.ali")
public class OssAliProperties {

    /**
     * 是否开启
     */
    private Boolean enable = false;

    /**
     * 域名
     */
    private String endpoint;

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