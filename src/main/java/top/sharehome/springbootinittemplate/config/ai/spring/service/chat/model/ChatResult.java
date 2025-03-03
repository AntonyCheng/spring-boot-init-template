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
    private String result;

    /**
     * 耗时（单位：毫秒）
     */
    private Long takeTime;

    /**
     * Token数
     */
    private Integer tokenNum;

    /**
     * 提示词
     */
    private Prompt prompt;

    public ChatResult(String result, Long takeTime, Integer tokenNum, String prompt) {
        this.prompt = new Prompt(new UserMessage(prompt));
        this.takeTime = takeTime;
        this.tokenNum = tokenNum;
        this.result = result;
    }

    public ChatResult(String result, Long takeTime, Integer tokenNum, Message... prompt) {
        this.prompt = new Prompt(prompt);
        this.takeTime = takeTime;
        this.tokenNum = tokenNum;
        this.result = result;
    }

    public ChatResult(String result, Long takeTime, Integer tokenNum, Prompt prompt) {
        this.prompt = prompt;
        this.takeTime = takeTime;
        this.tokenNum = tokenNum;
        this.result = result;
    }

    @Serial
    private static final long serialVersionUID = -2953969518518794478L;

}
