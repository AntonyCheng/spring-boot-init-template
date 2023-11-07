package top.sharehome.springbootinittemplate.utils.rabbitmq;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMq;
import top.sharehome.springbootinittemplate.config.rabbitmq.RabbitMqConfiguration;
import top.sharehome.springbootinittemplate.utils.rabbitmq.model.RabbitMqMessage;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

/**
 * RabbitMq工具类
 *
 * @author AntonyCheng
 */
@Component
@ConditionalOnBean(RabbitMqConfiguration.class)
@Slf4j
public class RabbitMqUtils {

    /**
     * 被封装的RabbitMq客户端
     */
    private static final RabbitTemplate RABBITMQ_TEMPLATE = SpringContextHolder.getBean(RabbitTemplate.class);

    /**
     * 向不带有死信队列的消息队列发送消息
     *
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void send(Object message, Class<? extends BaseCustomizeMq> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMq", className)) {
            String exchangeName = null;
            String bindingRouteKey = null;
            try {
                Field exchangeNameField = rabbitmqClass.getField("EXCHANGE_NAME");
                exchangeName = (String) exchangeNameField.get(null);
                Field bindingRouteKeyField = rabbitmqClass.getField("BINDING_ROUTING_KEY");
                bindingRouteKey = (String) bindingRouteKeyField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq [{}] is incorrect", className);
                return;
            }
            String msgText = JSON.toJSONString(message);
            RabbitMqMessage rabbitMqMessage = new RabbitMqMessage();
            rabbitMqMessage.setMsgId(UUID.randomUUID().toString());
            rabbitMqMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMqMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeName, bindingRouteKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq [{}] is incorrect", className);
        }
    }

    /**
     * 从不带有死信队列的消息队列获取消息
     *
     * @param rabbitmqClass 消息队列类
     * @return 返回消息体
     */
    public static RabbitMqMessage receive(Class<? extends BaseCustomizeMq> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        String queueName = null;
        if (!StringUtils.equals("BaseCustomizeMq", className)) {
            try {
                Field queueNameField = rabbitmqClass.getField("QUEUE_NAME");
                queueName = (String) queueNameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq [{}] is incorrect", className);
                return null;
            }
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq [{}] is incorrect", className);
            return null;
        }
        return JSON.parseObject(Objects.requireNonNull(RABBITMQ_TEMPLATE.receiveAndConvert(queueName)).toString(), RabbitMqMessage.class);
    }

}