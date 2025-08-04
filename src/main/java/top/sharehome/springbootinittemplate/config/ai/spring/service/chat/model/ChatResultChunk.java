package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * Chat分块集
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class ChatResultChunk implements Serializable {

    /**
     * 结果
     */
    private String content;

    /**
     * 思考内容
     */
    private String reasoningContent;

    public ChatResultChunk(String content, String reasoningContent) {
        this.content = content;
        this.reasoningContent = reasoningContent;
    }

    @Serial
    private static final long serialVersionUID = 1282354402547802177L;

}
