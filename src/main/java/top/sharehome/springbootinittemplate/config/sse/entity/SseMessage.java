package top.sharehome.springbootinittemplate.config.sse.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * SSE消息体
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class SseMessage implements Serializable {

    /**
     * 消息ID
     */
    private String id;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 消息主体
     */
    private Object data;

    /**
     * 消息附加信息
     */
    private String extra;

    @Serial
    private static final long serialVersionUID = -6839823345996159827L;

}
