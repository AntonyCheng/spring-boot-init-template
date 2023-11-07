package top.sharehome.springbootinittemplate.config.rabbitmq.customize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;
import top.sharehome.springbootinittemplate.config.rabbitmq.base.BaseMq;

/**
 * 消息队列实例的样例配置
 * todo 建议自定义不带有死信队列的消息队列实例时统一类名：xxxRabbitMq，同时类注解和类结构保持和customize/DemoRabbitMq一致，便于日志查看；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
public class DemoRabbitMq extends BaseMq {

}
