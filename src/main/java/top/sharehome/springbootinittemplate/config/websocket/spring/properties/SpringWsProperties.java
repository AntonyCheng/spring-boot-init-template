package top.sharehome.springbootinittemplate.config.websocket.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring WebSocket配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "websocket.spring")
public class SpringWsProperties {

    /**
     * 是否启用WebSocket服务
     */
    private Boolean enable = false;

    /**
     * WebSocket路径
     */
    private String path = "/websocket";

    /**
     * 跨域，设置访问源地址
     */
    private String allowedOrigins = "*";

}
