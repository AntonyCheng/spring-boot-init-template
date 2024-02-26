package top.sharehome.springbootinittemplate.config.rabbitmq.consumerExample;

import com.alibaba.fastjson2.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import top.sharehome.springbootinittemplate.config.rabbitmq.consumerExample.model.RabbitMessage;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMq;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMqWithDelay;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMqWithDlx;

import java.io.IOException;

/**
 * 消息队列RabbitMQ消费端编码示例
 * todo 需要测试时打开@Component注解
 *
 * @author AntonyCheng
 */
//@Component
public class RabbitMqConsumer {

    /**
     * 使用不带有死信队列的消息队列时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag     当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = DefaultRabbitMq.QUEUE_NAME)
    public void messageConsumer1(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            RabbitMessage rabbitMessage1 = JSON.parseObject(message.getBody(), RabbitMessage.class);
            System.out.println("rabbitMessage1 = " + rabbitMessage1);
            // 模拟出现异常
            // System.out.println(1 / 0);
            // 手动确认
            // 参数1：消息的tag
            // 参数2：多条处理
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 如果出现异常的情况下 根据实际情况重发
            // 重发一次后，丢失
            // 参数1：消息的tag
            // 参数2：多条处理
            // 参数3：重发
            //      false 不会重发，会把消息丢弃
            //      true 重发，建议不使用try/catch 否则会死循环
            // 手动拒绝消息
            System.out.println(e.getMessage());
            channel.basicNack(tag, false, false);
        }
    }

    /**
     * 使用带有死信队列的消息队列时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag     当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = DefaultRabbitMqWithDlx.QUEUE_WITH_DLX_NAME)
    public void messageConsumer2(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            RabbitMessage rabbitMessage2 = JSON.parseObject(message.getBody(), RabbitMessage.class);
            System.out.println("rabbitMessage2 = " + rabbitMessage2);
            // 模拟出现异常
            // System.out.println(1 / 0);
            // 手动确认
            // 参数1：消息的tag
            // 参数2：多条处理
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 如果出现异常的情况下 根据实际情况重发
            // 重发一次后，丢失
            // 参数1：消息的tag
            // 参数2：多条处理
            // 参数3：重发
            //      false 不会重发，会把消息打入到死信队列
            //      true 重发，建议不使用try/catch 否则会死循环
            // 手动拒绝消息
            System.out.println(e.getMessage());
            channel.basicNack(tag, false, false);
        }
    }

    /**
     * 使用带有死信队列的消息队列获取死信队列中的消息时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag     当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = DefaultRabbitMqWithDlx.DLX_QUEUE_WITH_DLX_NAME)
    public void messageConsumer3(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            RabbitMessage rabbitMessage3 = JSON.parseObject(message.getBody(), RabbitMessage.class);
            System.out.println("rabbitMessage3 = " + rabbitMessage3);
            // 模拟出现异常
            // System.out.println(1 / 0);
            // 手动确认
            // 参数1：消息的tag
            // 参数2：多条处理
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 如果出现异常的情况下 根据实际情况重发
            // 重发一次后，丢失
            // 参数1：消息的tag
            // 参数2：多条处理
            // 参数3：重发
            //      false 不会重发，会把消息打入到死信队列
            //      true 重发，建议不使用try/catch 否则会死循环
            // 手动拒绝消息
            System.out.println(e.getMessage());
            channel.basicNack(tag, false, false);
        }
    }

    /**
     * 使用延时队列时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag     当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = DefaultRabbitMqWithDelay.DLX_QUEUE_WITH_DELAY_NAME)
    public void messageConsumer4(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            RabbitMessage rabbitMessage4 = JSON.parseObject(message.getBody(), RabbitMessage.class);
            System.out.println("rabbitMessage4 = " + rabbitMessage4);
            // 模拟出现异常
            // System.out.println(1 / 0);
            // 手动确认
            // 参数1：消息的tag
            // 参数2：多条处理
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 如果出现异常的情况下 根据实际情况重发
            // 重发一次后，丢失
            // 参数1：消息的tag
            // 参数2：多条处理
            // 参数3：重发
            //      false 不会重发，会把消息丢弃
            //      true 重发，建议不使用try/catch 否则会死循环
            // 手动拒绝消息
            System.out.println(e.getMessage());
            channel.basicNack(tag, false, false);
        }
    }

}