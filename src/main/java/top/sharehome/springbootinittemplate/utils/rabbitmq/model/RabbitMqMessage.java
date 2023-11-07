package top.sharehome.springbootinittemplate.utils.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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