package top.sharehome.springbootinittemplate.config.rabbitmq.customize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMqWithDlx;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息队列实例的样例配置
 * todo 建议自定义带有死信队列的消息队列实例时统一类名：xxxRabbitMqWithDLX，同时类注解、类结构以及类静态变量名和customize/DemoRabbitMqWithDLX保持一致，便于日志查看以及使用工具类；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
public class DemoRabbitMqWithDlx extends BaseCustomizeMqWithDlx {

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
    public Exchange demoExchangeWithDlx() {
        return new TopicExchange(EXCHANGE_WITH_DLX_NAME, true, false, null);
    }

    /**
     * 注册实例死信队列交换机
     */
    @Bean
    public Exchange demoDxlExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME, true, false, null);
    }

    /**
     * 注册实例队列，同时配置死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean
    public Queue demoQueueWithDlx(ConnectionFactory connectionFactory) {
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
    public Queue demoDxlQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(DLX_QUEUE_NAME, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }


    /**
     * 注册实例队列绑定关系
     *
     * @param demoQueueWithDlx    注册实例队列方法名称
     * @param demoExchangeWithDlx 注册实例交换机方法名称
     * @return 返回结果
     */
    @Bean
    public Binding demoBinding(Queue demoQueueWithDlx, Exchange demoExchangeWithDlx) {
        return BindingBuilder
                .bind(demoQueueWithDlx)
                .to(demoExchangeWithDlx)
                .with(BINDING_WITH_DLX_ROUTING_KEY)
                .noargs();
    }

    /**
     * 注册实例死信队列绑定关系
     *
     * @param demoDxlQueue    注册实例死信队列方法名称
     * @param demoDxlExchange 注册实例死信队列交换机方法名称
     * @return 返回结果
     */
    @Bean
    public Binding demoDxlBinding(Queue demoDxlQueue, Exchange demoDxlExchange) {
        return BindingBuilder
                .bind(demoDxlQueue)
                .to(demoDxlExchange)
                .with(DLX_BINDING_ROUTING_KEY)
                .noargs();
    }

}