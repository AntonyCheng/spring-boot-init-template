package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.Serial;
import java.io.Serializable;

/**
 * Chat结果集
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class ChatResult implements Serializable {

    /**
     * 结果
     */
    private String content;

    /**
     * 思考内容
     */
    private String reasoningContent;

    /**
     * 耗时（单位：毫秒）
     */
    private Long time;

    /**
     * Token数
     */
    private Integer usage;

    /**
     * 提示词
     */
    private Prompt prompt;

    public ChatResult(String content, Long time, Integer usage, String prompt) {
        this.content = content;
        this.time = time;
        this.usage = usage;
        this.prompt = new Prompt(new UserMessage(prompt));
    }

    public ChatResult(String content, Long time, Integer usage, Message... prompt) {
        this.content = content;
        this.time = time;
        this.usage = usage;
        this.prompt = new Prompt(prompt);
    }

    public ChatResult(String content, Long time, Integer usage, Prompt prompt) {
        this.content = content;
        this.time = time;
        this.usage = usage;
        this.prompt = prompt;
    }

    @Serial
    private static final long serialVersionUID = -2953969518518794478L;

}
