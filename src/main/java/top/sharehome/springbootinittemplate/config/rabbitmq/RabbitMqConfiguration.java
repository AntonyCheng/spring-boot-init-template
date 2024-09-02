package top.sharehome.springbootinittemplate.config.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.rabbitmq.condition.RabbitMqCondition;

/**
 * 消息队列RabbitMQ实例配置
 *
 * @author AntonyCheng
 */
@Slf4j
@Configuration
@Conditional(RabbitMqCondition.class)
public class RabbitMqConfiguration {

    /**
     * 注册RabbitTemplate实例
     *
     * @param connectionFactory 连接工厂接口
     * @return 返回结果
     */
    @Bean("rabbitTemplateBean")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数(设置消息进入交换机后未被队列接收的消息不被丢弃由broker保存,false为丢弃)
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReceiveTimeout(30000);
        rabbitTemplate.setReplyTimeout(30000);
        //为了避免解除单例模式，这里将交换机回调和队列回调写进配置类中，这样就不用去设置非单例模式
        //确认消息送到交换机(Exchange)回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info(">>>>>>>>>> 确认消息成功送到交换机(Exchange)，相关数据：{}", correlationData);
            } else {
                log.error(">>>>>>>>>> 确认消息没能送到交换机(Exchange)，相关数据：{}，错误原因：{}", correlationData, cause);
            }
        });
        //确认消息送到队列(Queue)回调，只有出错了才会有响应。
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.error(">>>>>>>>>> 确认消息没能送到队列(Queue)，发生消息：{}，回应码：{}，回应信息：{}，交换机：{}，路由键值：{}",
                    returnedMessage.getMessage(),
                    returnedMessage.getReplyCode(),
                    returnedMessage.getReplyText(),
                    returnedMessage.getExchange(),
                    returnedMessage.getRoutingKey());
        });
        return rabbitTemplate;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}