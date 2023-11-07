package top.sharehome.springbootinittemplate.config.rabbitmq.customize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;
import top.sharehome.springbootinittemplate.config.rabbitmq.base.BaseMq;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息队列实例的样例配置
 * todo 建议自定义带有死信队列的消息队列实例时统一类名：xxxRabbitMqWithDLX，同时类注解和类结构保持和customize/DemoRabbitMqWithDLX一致，便于日志查看；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
public class DemoRabbitMqWithDLX extends BaseMq {

    /**
     * 实例交换机名称
     */
    public static final String EXCHANGE_WITH_DLX_NAME = "exchange.with.dlx.demo";

    /**
     * 实例队列名称
     */
    public static final String QUEUE_WITH_DLX_NAME = "queue.with.dlx.demo";

    /**
     * 实例队列绑定关系的RoutingKey
     */
    public static final String BINDING_WITH_DLX_ROUTING_KEY = "binding.with.dlx.routing.key.demo";

    /**
     * 实例死信队列交换机名称
     */
    public static final String DLX_EXCHANGE_NAME = "dlx.exchange.demo";

    /**
     * 实例死信队列名称
     */
    public static final String DLX_QUEUE_NAME = "dlx.queue.demo";

    /**
     * 实例死信队列绑定关系的RoutingKey
     */
    public static final String DLX_BINDING_ROUTING_KEY = "dlx.binding.routing.key.demo";

    /**
     * 注册实例交换机
     *
     * @return 返回交换机实例
     */
    @Bean
    public Exchange demoExchange() {
        return new TopicExchange(EXCHANGE_WITH_DLX_NAME, true, false, null);
    }

    /**
     * 注册实例死信队列交换机
     */
    @Bean
    public Exchange demoDeadLetterExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME, true, false, null);
    }

    /**
     * 注册实例队列，同时配置死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean
    public Queue demoQueue(ConnectionFactory connectionFactory) {
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
     * 注册实例死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean
    public Queue demoDeadLetterQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(DLX_QUEUE_NAME, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }


    /**
     * 注册实例队列绑定关系
     *
     * @param demoQueue    注册实例队列方法名称
     * @param demoExchange 注册实例交换机方法名称
     * @return 返回结果
     */
    @Bean
    public Binding demoBinding(Queue demoQueue, Exchange demoExchange) {
        return BindingBuilder
                .bind(demoQueue)
                .to(demoExchange)
                .with(BINDING_WITH_DLX_ROUTING_KEY)
                .noargs();
    }

    /**
     * 注册实例死信队列绑定关系
     *
     * @param demoDeadLetterQueue    注册实例死信队列方法名称
     * @param demoDeadLetterExchange 注册实例死信队列交换机方法名称
     * @return 返回结果
     */
    @Bean
    public Binding demoDeadLetterBinding(Queue demoDeadLetterQueue, Exchange demoDeadLetterExchange) {
        return BindingBuilder
                .bind(demoDeadLetterQueue)
                .to(demoDeadLetterExchange)
                .with(DLX_BINDING_ROUTING_KEY)
                .noargs();
    }

}