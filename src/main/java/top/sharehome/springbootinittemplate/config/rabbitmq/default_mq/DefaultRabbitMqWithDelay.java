package top.sharehome.springbootinittemplate.config.rabbitmq.default_mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMqWithDelay;
import top.sharehome.springbootinittemplate.config.rabbitmq.condition.RabbitMqCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的延迟队列实例配置
 * todo 建议自定义延迟队列实例时统一类名：xxxRabbitMqWithDelay，同时类注解、类结构以及类静态变量名和default_mq/DefaultRabbitMqWithDelay保持一致，便于日志查看以及使用工具类，最简单的创建方式就是复制default_mq/DefaultRabbitMqWithDelay到customize_mq中全局修改文件中的“default”单词；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(RabbitMqCondition.class)
public class DefaultRabbitMqWithDelay extends BaseCustomizeMqWithDelay {

    /**
     * 默认的延迟队列实例交换机名称
     */
    public static final String EXCHANGE_WITH_DELAY_NAME = "exchange.with.delay.default";

    /**
     * 默认的延迟队列实例队列名称
     */
    public static final String QUEUE_WITH_DELAY_NAME = "queue.with.delay.default";

    /**
     * 默认的延迟队列实例队列绑定关系的RoutingKey
     */
    public static final String BINDING_WITH_DELAY_ROUTING_KEY = "binding.with.delay.routing.key.default";

    /**
     * 默认的延迟队列实例死信队列交换机名称
     */
    public static final String DELAY_DLX_EXCHANGE_NAME = "delay.dlx.exchange.default";

    /**
     * 默认的延迟队列实例死信队列名称
     */
    public static final String DELAY_DLX_QUEUE_NAME = "delay.dlx.queue.default";

    /**
     * 默认的延迟队列实例死信队列绑定关系的RoutingKey
     */
    public static final String DELAY_DLX_BINDING_ROUTING_KEY = "delay.dlx.binding.routing.key.default";

    /**
     * 默认的延迟队列实例延迟时间（默认60000毫秒）
     */
    public static final long TTL = 60000;

    /**
     * 注册默认的延迟队列实例交换机
     *
     * @return 返回交换机实例
     */
    @Bean("defaultExchangeWithDelay")
    public Exchange defaultExchangeWithDelay() {
        return new TopicExchange(EXCHANGE_WITH_DELAY_NAME, true, false, null);
    }

    /**
     * 注册默认的延迟队列实例死信队列交换机
     */
    @Bean("defaultDelayDxlExchange")
    public Exchange defaultDelayDxlExchange() {
        return new TopicExchange(DELAY_DLX_EXCHANGE_NAME, true, false, null);
    }

    /**
     * 注册默认的延迟队列实例队列，同时配置死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("defaultQueueWithDelay")
    public Queue defaultQueueWithDelay(ConnectionFactory connectionFactory) {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DELAY_DLX_EXCHANGE_NAME);
        // x-dead-letter-routing-key 这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DELAY_DLX_BINDING_ROUTING_KEY);
        // x-message-ttl             这里声明队列的TTL
        args.put("x-message-ttl", TTL);
        Queue queue = new Queue(QUEUE_WITH_DELAY_NAME, true, false, false, args);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 注册默认的延迟队列实例死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("defaultDelayDxlQueue")
    public Queue defaultDelayDxlQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(DELAY_DLX_QUEUE_NAME, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }


    /**
     * 注册默认的延迟队列实例队列绑定关系
     *
     * @param defaultQueueWithDelay    注册实例队列方法名称
     * @param defaultExchangeWithDelay 注册实例交换机方法名称
     * @return 返回结果
     */
    @Bean("defaultBindingWithDelay")
    public Binding defaultBindingWithDelay(@Qualifier("defaultQueueWithDelay") Queue defaultQueueWithDelay, @Qualifier("defaultExchangeWithDelay") Exchange defaultExchangeWithDelay) {
        return BindingBuilder
                .bind(defaultQueueWithDelay)
                .to(defaultExchangeWithDelay)
                .with(BINDING_WITH_DELAY_ROUTING_KEY)
                .noargs();
    }

    /**
     * 注册默认的延迟队列实例死信队列绑定关系
     *
     * @param defaultDxlQueue    注册实例死信队列方法名称
     * @param defaultDxlExchange 注册实例死信队列交换机方法名称
     * @return 返回结果
     */
    @Bean("defaultDelayDxlBinding")
    public Binding defaultDelayDxlBinding(@Qualifier("defaultDelayDxlQueue") Queue defaultDxlQueue, @Qualifier("defaultDelayDxlExchange") Exchange defaultDxlExchange) {
        return BindingBuilder
                .bind(defaultDxlQueue)
                .to(defaultDxlExchange)
                .with(DELAY_DLX_BINDING_ROUTING_KEY)
                .noargs();
    }

}