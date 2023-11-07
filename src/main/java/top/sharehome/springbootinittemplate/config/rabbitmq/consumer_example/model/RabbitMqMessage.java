package top.sharehome.springbootinittemplate.config.rabbitmq.consumer_example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息队列消息基类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMqMessage {

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息内容
     */
    private String msgText;

}