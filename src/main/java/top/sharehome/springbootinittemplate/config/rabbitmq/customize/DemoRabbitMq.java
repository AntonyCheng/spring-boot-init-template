package top.sharehome.springbootinittemplate.config.rabbitmq.customize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMq;

/**
 * 消息队列实例的样例配置
 * todo 建议自定义不带有死信队列的消息队列实例时统一类名：xxxRabbitMq，同时类注解、类结构以及类静态变量名和customize/DemoRabbitMq保持一致，便于日志查看以及使用工具类；
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
public class DemoRabbitMq extends BaseCustomizeMq {

    /**
     * 实例交换机名称
     */
    public static final String EXCHANGE_NAME = "exchange.demo";

    /**
     * 实例队列名称
     */
    public static final String QUEUE_NAME = "queue.demo";

    /**
     * 实例队列绑定关系的RoutingKey
     */
    public static final String BINDING_ROUTING_KEY = "binding.routing.key.demo";

    /**
     * 注册实例交换机
     *
     * @return 返回交换机实例
     */
    @Bean
    public Exchange demoExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false, null);
    }


    /**
     * 注册实例队列，同时配置死信队列
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean
    public Queue demoQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(QUEUE_NAME, true, false, false, null);
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
                .with(BINDING_ROUTING_KEY)
                .noargs();
    }

}