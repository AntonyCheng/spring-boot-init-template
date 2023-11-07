package top.sharehome.springbootinittemplate.config.rabbitmq.base;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息队列消息基类
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class BaseMsg {

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息内容
     */
    private String msgText;

}
