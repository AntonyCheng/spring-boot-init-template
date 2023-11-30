package top.sharehome.springbootinittemplate.utils.rabbitmq;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMq;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMqWithDelay;
import top.sharehome.springbootinittemplate.config.rabbitmq.BaseCustomizeMqWithDlx;
import top.sharehome.springbootinittemplate.config.rabbitmq.condition.RabbitMqCondition;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMq;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMqWithDelay;
import top.sharehome.springbootinittemplate.config.rabbitmq.defaultMq.DefaultRabbitMqWithDlx;
import top.sharehome.springbootinittemplate.config.rabbitmq.properties.RabbitMqProperties;
import top.sharehome.springbootinittemplate.utils.rabbitmq.model.RabbitMessage;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

/**
 * RabbitMq工具类
 *
 * @author AntonyCheng
 */
@Component
@Conditional(RabbitMqCondition.class)
@EnableConfigurationProperties(RabbitMqProperties.class)
@AllArgsConstructor
@Slf4j
public class RabbitMqUtils {

    /**
     * 被封装的RabbitMq客户端
     */
    private static final RabbitTemplate RABBITMQ_TEMPLATE = SpringContextHolder.getBean("rabbitTemplateBean", RabbitTemplate.class);

    private final RabbitMqProperties rabbitMqProperties;

    private static long maxAwaitTimeout;

    @PostConstruct
    private void setMaxAwaitTimeout() {
        maxAwaitTimeout = rabbitMqProperties.getMaxAwaitTimeout();
    }

    /**
     * 向默认的不带有死信队列的消息队列发送消息
     *
     * @param message 消息体
     */
    public static void defaultSendMsg(Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(UUID.randomUUID().toString());
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMq.EXCHANGE_NAME, DefaultRabbitMq.BINDING_ROUTING_KEY, sendRes);
    }

    /**
     * 向默认的不带有死信队列的消息队列发送消息
     *
     * @param msgId   消息ID
     * @param message 消息体
     */
    public static void defaultSendMsg(String msgId, Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(msgId);
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMq.EXCHANGE_NAME, DefaultRabbitMq.BINDING_ROUTING_KEY, sendRes);
    }

    /**
     * 从默认的不带有死信队列的消息队列获取消息
     *
     * @return 返回消息体
     */
    public static RabbitMessage defaultReceiveMsg() {
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(DefaultRabbitMq.QUEUE_NAME, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

    /**
     * 向默认的带有死信队列的消息队列发送消息
     *
     * @param message 消息体
     */
    public static void defaultSendMsgWithDlx(Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(UUID.randomUUID().toString());
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMqWithDlx.EXCHANGE_WITH_DLX_NAME, DefaultRabbitMqWithDlx.BINDING_WITH_DLX_ROUTING_KEY, sendRes);
    }

    /**
     * 向默认的带有死信队列的消息队列发送消息
     *
     * @param msgId   消息ID
     * @param message 消息体
     */
    public static void defaultSendMsgWithDlx(String msgId, Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(msgId);
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMqWithDlx.EXCHANGE_WITH_DLX_NAME, DefaultRabbitMqWithDlx.BINDING_WITH_DLX_ROUTING_KEY, sendRes);
    }

    /**
     * 从默认的带有死信队列的消息队列获取消息
     *
     * @return 返回消息体
     */
    public static RabbitMessage defaultReceiveMsgWithDlx() {
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(DefaultRabbitMqWithDlx.QUEUE_WITH_DLX_NAME, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

    /**
     * 从默认的消息队列的死信队列中获取消息
     *
     * @return 返回消息体
     */
    public static RabbitMessage defaultReceiveMsgWithDlxInDlx() {
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(DefaultRabbitMqWithDlx.DLX_QUEUE_WITH_DLX_NAME, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(Objects.requireNonNull(res).toString(), RabbitMessage.class);
    }

    /**
     * 向默认的延迟队列发送消息
     *
     * @param message 消息体
     */
    public static void defaultSendMsgWithDelay(Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(UUID.randomUUID().toString());
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMqWithDelay.EXCHANGE_WITH_DELAY_NAME, DefaultRabbitMqWithDelay.BINDING_WITH_DELAY_ROUTING_KEY, sendRes);
    }

    /**
     * 向默认的延迟队列发送消息
     *
     * @param msgId   消息ID
     * @param message 消息体
     */
    public static void defaultSendMsgWithDelay(String msgId, Object message) {
        String msgText = JSON.toJSONString(message);
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setMsgId(msgId);
        rabbitMessage.setMsgText(msgText);
        String sendRes = JSON.toJSONString(rabbitMessage);
        RABBITMQ_TEMPLATE.convertAndSend(DefaultRabbitMqWithDelay.EXCHANGE_WITH_DELAY_NAME, DefaultRabbitMqWithDelay.BINDING_WITH_DELAY_ROUTING_KEY, sendRes);
    }

    /**
     * 从默认的延迟队列获取消息
     *
     * @return 返回消息体
     */
    public static RabbitMessage defaultReceiveMsgWithDelay() {
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(DefaultRabbitMqWithDelay.DLX_QUEUE_WITH_DELAY_NAME, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

    /**
     * 向不带有死信队列的消息队列发送消息
     *
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsg(Object message, Class<? extends BaseCustomizeMq> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMq", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeName;
            String bindingRouteKey;
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
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(UUID.randomUUID().toString());
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeName, bindingRouteKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq [{}] is incorrect", className);
        }
    }

    /**
     * 向不带有死信队列的消息队列发送消息
     *
     * @param msgId         消息ID
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsg(String msgId, Object message, Class<? extends BaseCustomizeMq> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMq", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeName;
            String bindingRouteKey;
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
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(msgId);
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
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
    public static RabbitMessage receiveMsg(Class<? extends BaseCustomizeMq> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        String queueName;
        if (!StringUtils.equals("BaseCustomizeMq", className.split(rabbitmqClass.getPackage().getName())[1])) {
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
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(queueName, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

    /**
     * 向带有死信队列的消息队列发送消息
     *
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsgWithDlx(Object message, Class<? extends BaseCustomizeMqWithDlx> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMqWithDlx", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeWithDlxName;
            String bindingWithDlxRoutingKey;
            try {
                Field exchangeWithDlxNameField = rabbitmqClass.getField("EXCHANGE_WITH_DLX_NAME");
                exchangeWithDlxName = (String) exchangeWithDlxNameField.get(null);
                Field bindingWithDlxRoutingKeyField = rabbitmqClass.getField("BINDING_WITH_DLX_ROUTING_KEY");
                bindingWithDlxRoutingKey = (String) bindingWithDlxRoutingKeyField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
                return;
            }
            String msgText = JSON.toJSONString(message);
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(UUID.randomUUID().toString());
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeWithDlxName, bindingWithDlxRoutingKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
        }
    }

    /**
     * 向带有死信队列的消息队列发送消息
     *
     * @param msgId         消息ID
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsgWithDlx(String msgId, Object message, Class<? extends BaseCustomizeMqWithDlx> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMqWithDlx", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeWithDlxName;
            String bindingWithDlxRoutingKey;
            try {
                Field exchangeWithDlxNameField = rabbitmqClass.getField("EXCHANGE_WITH_DLX_NAME");
                exchangeWithDlxName = (String) exchangeWithDlxNameField.get(null);
                Field bindingWithDlxRoutingKeyField = rabbitmqClass.getField("BINDING_WITH_DLX_ROUTING_KEY");
                bindingWithDlxRoutingKey = (String) bindingWithDlxRoutingKeyField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
                return;
            }
            String msgText = JSON.toJSONString(message);
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(msgId);
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeWithDlxName, bindingWithDlxRoutingKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
        }
    }

    /**
     * 从带有死信队列的消息队列获取消息
     *
     * @param rabbitmqClass 消息队列类
     * @return 返回消息体
     */
    public static RabbitMessage receiveMsgWithDlx(Class<? extends BaseCustomizeMqWithDlx> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        String queueWithDlxName;
        if (!StringUtils.equals("BaseCustomizeMqWithDlx", className.split(rabbitmqClass.getPackage().getName())[1])) {
            try {
                Field queueWithDlxNameField = rabbitmqClass.getField("QUEUE_WITH_DLX_NAME");
                queueWithDlxName = (String) queueWithDlxNameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
                return null;
            }
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
            return null;
        }
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(queueWithDlxName, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

    /**
     * 从消息队列的死信队列中获取消息
     *
     * @param rabbitmqClass 消息队列类
     * @return 返回消息体
     */
    public static RabbitMessage receiveMsgWithDlxInDlx(Class<? extends BaseCustomizeMqWithDlx> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        String dlxQueueName;
        if (!StringUtils.equals("BaseCustomizeMqWithDlx", className.split(rabbitmqClass.getPackage().getName())[1])) {
            try {
                Field dlxQueueNameField = rabbitmqClass.getField("DLX_QUEUE_NAME");
                dlxQueueName = (String) dlxQueueNameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
                return null;
            }
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with dlx [{}] is incorrect", className);
            return null;
        }
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(dlxQueueName, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(Objects.requireNonNull(res).toString(), RabbitMessage.class);
    }

    /**
     * 向延迟队列发送消息
     *
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsgWithDelay(Object message, Class<? extends BaseCustomizeMqWithDelay> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMqWithDelay", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeWithDelayName;
            String bindingWithDelayRoutingKey;
            try {
                Field exchangeWithDelayNameField = rabbitmqClass.getField("EXCHANGE_WITH_DELAY_NAME");
                exchangeWithDelayName = (String) exchangeWithDelayNameField.get(null);
                Field bindingWithDelayRoutingKeyField = rabbitmqClass.getField("BINDING_WITH_DELAY_ROUTING_KEY");
                bindingWithDelayRoutingKey = (String) bindingWithDelayRoutingKeyField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
                return;
            }
            String msgText = JSON.toJSONString(message);
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(UUID.randomUUID().toString());
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeWithDelayName, bindingWithDelayRoutingKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
        }
    }

    /**
     * 向延迟队列发送消息
     *
     * @param msgId         消息ID
     * @param message       消息体
     * @param rabbitmqClass 消息队列类
     */
    public static void sendMsgWithDelay(String msgId, Object message, Class<? extends BaseCustomizeMqWithDelay> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        if (!StringUtils.equals("BaseCustomizeMqWithDelay", className.split(rabbitmqClass.getPackage().getName())[1])) {
            String exchangeWithDelayName;
            String bindingWithDelayRoutingKey;
            try {
                Field exchangeWithDelayNameField = rabbitmqClass.getField("EXCHANGE_WITH_DELAY_NAME");
                exchangeWithDelayName = (String) exchangeWithDelayNameField.get(null);
                Field bindingWithDelayRoutingKeyField = rabbitmqClass.getField("BINDING_WITH_DELAY_ROUTING_KEY");
                bindingWithDelayRoutingKey = (String) bindingWithDelayRoutingKeyField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
                return;
            }
            String msgText = JSON.toJSONString(message);
            RabbitMessage rabbitMessage = new RabbitMessage();
            rabbitMessage.setMsgId(msgId);
            rabbitMessage.setMsgText(msgText);
            String sendRes = JSON.toJSONString(rabbitMessage);
            RABBITMQ_TEMPLATE.convertAndSend(exchangeWithDelayName, bindingWithDelayRoutingKey, sendRes);
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
        }
    }

    /**
     * 从延迟队列获取消息
     *
     * @param rabbitmqClass 消息队列类
     * @return 返回消息体
     */
    public static RabbitMessage receiveMsgWithDelay(Class<? extends BaseCustomizeMqWithDelay> rabbitmqClass) {
        String className = rabbitmqClass.getName();
        String delayDlxQueueName;
        if (!StringUtils.equals("BaseCustomizeMqWithDelay", className.split(rabbitmqClass.getPackage().getName())[1])) {
            try {
                Field delayDlxQueueNameField = rabbitmqClass.getField("DELAY_DLX_QUEUE_NAME");
                delayDlxQueueName = (String) delayDlxQueueNameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
                return null;
            }
        } else {
            log.error(">>>>>>>>>> the custom rabbitmq with delay [{}] is incorrect", className);
            return null;
        }
        Object res = RABBITMQ_TEMPLATE.receiveAndConvert(delayDlxQueueName, maxAwaitTimeout);
        if (ObjectUtils.isEmpty(res)) {
            return null;
        }
        return JSON.parseObject(res.toString(), RabbitMessage.class);
    }

}