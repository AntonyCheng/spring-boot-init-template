package top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMq;
import top.sharehome.springbootinittemplate.config.rabbitmq.condition.RabbitMqCondition;

/**
 * 默认的不带有死信队列的消息队列实例配置
 * todo 建议自定义不带有死信队列的消息队列实例时统一类名：xxxRabbitMq，同时类注解、类结构以及类静态变量名和default_mq/DefaultRabbitMq保持一致，便于日志查看以及使用工具类，最简单的创建方式就是复制default_mq/DefaultRabbitMq到customize_mq中全局修改文件中的“default”单词；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(RabbitMqCondition.class)
public class DefaultRabbitMq extends BaseCustomizeMq {

    /**
     * 默认的实例交换机名称
     */
    public static final String EXCHANGE_NAME = "exchange.default";

    /**
     * 默认的实例队列名称
     */
    public static final String QUEUE_NAME = "queue.default";

    /**
     * 默认的实例队列绑定关系的RoutingKey
     */
    public static final String BINDING_ROUTING_KEY = "binding.routing.key.default";

    /**
     * 注册默认的实例交换机
     *
     * @return 返回交换机实例
     */
    @Bean("defaultExchange")
    public Exchange defaultExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false, null);
    }


    /**
     * 注册默认的实例队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("defaultQueue")
    public Queue defaultQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(QUEUE_NAME, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 注册默认的实例队列绑定关系
     *
     * @param defaultQueue    注册实例队列方法名称
     * @param defaultExchange 注册实例交换机方法名称
     * @return 返回结果
     */
    @Bean("defaultBinding")
    public Binding defaultBinding(@Qualifier("defaultQueue") Queue defaultQueue, @Qualifier("defaultExchange") Exchange defaultExchange) {
        return BindingBuilder
                .bind(defaultQueue)
                .to(defaultExchange)
                .with(BINDING_ROUTING_KEY)
                .noargs();
    }

}