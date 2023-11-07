package top.sharehome.springbootinittemplate.utils.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;
import top.sharehome.springbootinittemplate.config.rabbitmq.customize.DemoRabbitMqWithDLX;

/**
 * RabbitMq工具类
 *
 * @author AntonyCheng
 */
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
public class RabbitMqUtils {

    /**
     * 被封装的RabbitMq客户端
     */
    private static final RabbitTemplate RABBITMQ_TEMPLATE = SpringContextHolder.getBean(RabbitTemplate.class);

    public static void test() {
        RABBITMQ_TEMPLATE.convertAndSend(DemoRabbitMqWithDLX.EXCHANGE_WITH_DLX_NAME, DemoRabbitMqWithDLX.BINDING_WITH_DLX_ROUTING_KEY,"test");
    }
}
