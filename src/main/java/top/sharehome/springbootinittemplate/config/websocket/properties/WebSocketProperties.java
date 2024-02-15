package top.sharehome.springbootinittemplate.config.websocket.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    /**
     * 是否启用WebSocket服务
     */
    private Boolean enable = false;

    /**
     * WebSocket端口，范围只允许为0-65535中的整数
     */
    private Integer port = 39999;

}
