package top.sharehome.springbootinittemplate.config.rabbitmq.default_mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMqWithDlx;
import top.sharehome.springbootinittemplate.config.rabbitmq.condition.RabbitMqCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的带有死信队列的消息队列实例配置
 * todo 建议自定义带有死信队列的消息队列实例时统一类名：xxxRabbitMqWithDLX，同时类注解、类结构以及类静态变量名和default_mq/DefaultRabbitMqWithDLX保持一致，便于日志查看以及使用工具类，最简单的创建方式就是复制default_mq/DefaultRabbitMqWithDLX到customize_mq中全局修改文件中的“default”单词；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(RabbitMqCondition.class)
public class DefaultRabbitMqWithDlx extends BaseCustomizeMqWithDlx {

    /**
     * 默认的实例交换机名称
     */
    public static final String EXCHANGE_WITH_DLX_NAME = "exchange.with.dlx.default";

    /**
     * 默认的实例队列名称
     */
    public static final String QUEUE_WITH_DLX_NAME = "queue.with.dlx.default";

    /**
     * 默认的实例队列绑定关系的RoutingKey
     */
    public static final String BINDING_WITH_DLX_ROUTING_KEY = "binding.with.dlx.routing.key.default";

    /**
     * 默认的实例死信队列交换机名称
     */
    public static final String DLX_EXCHANGE_NAME = "dlx.exchange.default";

    /**
     * 默认的实例死信队列名称
     */
    public static final String DLX_QUEUE_NAME = "dlx.queue.default";

    /**
     * 默认的实例死信队列绑定关系的RoutingKey
     */
    public static final String DLX_BINDING_ROUTING_KEY = "dlx.binding.routing.key.default";

    /**
     * 注册默认的实例交换机
     *
     * @return 返回交换机实例
     */
    @Bean("defaultExchangeWithDlx")
    public Exchange defaultExchangeWithDlx() {
        return new TopicExchange(EXCHANGE_WITH_DLX_NAME, true, false, null);
    }

    /**
     * 注册默认的实例死信队列交换机
     */
    @Bean("defaultDxlExchange")
    public Exchange defaultDxlExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME, true, false, null);
    }

    /**
     * 注册默认的实例队列，同时配置死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("defaultQueueWithDlx")
    public Queue defaultQueueWithDlx(ConnectionFactory connectionFactory) {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        // x-dead-letter-routing-key   这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DLX_BINDING_ROUTING_KEY);
        Queue queue = new Queue(QUEUE_WITH_DLX_NAME, true, false, false, args);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 注册默认的实例死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("defaultDxlQueue")
    public Queue defaultDxlQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(DLX_QUEUE_NAME, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }


    /**
     * 注册默认的实例队列绑定关系
     *
     * @param defaultQueueWithDlx    注册实例队列方法名称
     * @param defaultExchangeWithDlx 注册实例交换机方法名称
     * @return 返回结果
     */
    @Bean("defaultBindingWithDlx")
    public Binding defaultBindingWithDlx(@Qualifier("defaultQueueWithDlx") Queue defaultQueueWithDlx, @Qualifier("defaultExchangeWithDlx") Exchange defaultExchangeWithDlx) {
        return BindingBuilder
                .bind(defaultQueueWithDlx)
                .to(defaultExchangeWithDlx)
                .with(BINDING_WITH_DLX_ROUTING_KEY)
                .noargs();
    }

    /**
     * 注册默认的实例死信队列绑定关系
     *
     * @param defaultDxlQueue    注册实例死信队列方法名称
     * @param defaultDxlExchange 注册实例死信队列交换机方法名称
     * @return 返回结果
     */
    @Bean("defaultDxlBinding")
    public Binding defaultDxlBinding(@Qualifier("defaultDxlQueue") Queue defaultDxlQueue, @Qualifier("defaultDxlExchange") Exchange defaultDxlExchange) {
        return BindingBuilder
                .bind(defaultDxlQueue)
                .to(defaultDxlExchange)
                .with(DLX_BINDING_ROUTING_KEY)
                .noargs();
    }

}