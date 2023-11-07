package top.sharehome.springbootinittemplate.config.rabbitmq.consumer_example;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import top.sharehome.springbootinittemplate.config.rabbitmq.consumer_example.model.RabbitMqMessage;

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
     * 使用带有死信队列的消息队列时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag 当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = "queue.with.dlx.demo")
    public void messageConsumer1(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            System.out.println(new String(message.getBody()));
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
            channel.basicNack(tag, false, false);
        }
    }

    /**
     * 使用不带有死信队列的消息队列时参考以下代码
     *
     * @param message 信息本体
     * @param channel 通道
     * @param tag 当前消息在队列中的索引
     * @throws IOException 抛出异常
     */
    @RabbitHandler
    @RabbitListener(queues = "queue.with.dlx.demo")
    public void messageConsumer2(RabbitMqMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        // todo 使用不带有死信队列的消息队列时参考以下代码
        try {
            System.out.println(message.getMsgId());
            System.out.println(JSON.parse(message.getMsgText()));
            // 模拟出现异常
            // System.out.println(1 / 0);
        } catch (Exception e) {
            // 如果出现异常的情况下 根据实际情况重发
            // 重发一次后，丢失
            // 参数1：消息的tag
            // 参数2：多条处理
            // 参数3：重发
            //      false 不会重发，会把消息打入到死信队列
            //      true 重发，建议不使用try/catch 否则会死循环
            // 手动拒绝消息
            channel.basicNack(tag, false, true);
            return;
        }
        // 手动确认
        // 参数1：消息的tag
        // 参数2：多条处理
        channel.basicAck(tag, false);
    }

}