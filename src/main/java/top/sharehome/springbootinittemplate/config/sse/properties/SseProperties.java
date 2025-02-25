package top.sharehome.springbootinittemplate.config.sse.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "sse")
public class SseProperties {

    /**
     * SSE访问路径（即/sse）
     */
    private String path = "sse";

    /**
     * SSE默认连接超时时间（单位：毫秒），0表示无限制
     */
    private Long timeout = 0L;

}
