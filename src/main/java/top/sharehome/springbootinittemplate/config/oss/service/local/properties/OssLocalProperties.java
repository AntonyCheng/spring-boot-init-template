package top.sharehome.springbootinittemplate.config.oss.service.local.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地OSS配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "oss.local")
public class OssLocalProperties {

    /**
     * 是否开启
     */
    private Boolean enable = false;

    /**
     * 本地存储根目录
     */
    private String path;

    /**
     * 本地存储IP地址
     */
    private String address = "127.0.0.1";

    /**
     * 本地存储IP协议是否为https
     */
    private Boolean isHttps = false;

    /**
     * 本地存储访问接口前缀
     */
    private String prefix = "download";

}