package top.sharehome.springbootinittemplate.config.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqProperties {

    /**
     * 是否开启RabbitMQ
     */
    private boolean enable;

}