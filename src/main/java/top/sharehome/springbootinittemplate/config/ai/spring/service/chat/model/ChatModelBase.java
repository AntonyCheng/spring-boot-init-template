package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatService;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * Chat模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public class ChatModelBase {

    /**
     * Chat模型服务方
     */
    private ChatService chatService;

    /**
     * 模型温度，默认0.8
     */
    @Setter
    private Double temperature;

    /**
     * 模型top-p，默认0.9
     */
    @Setter
    private Double topP;

    public ChatModelBase(ChatService chatService, Double temperature, Double topP) {
        if (Objects.isNull(chatService)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatService]不能为空");
        }
        this.chatService = chatService;
        this.temperature = Objects.isNull(temperature) ? 0.8 : temperature;
        this.topP = Objects.isNull(topP) ? 0.9 : temperature;
    }

}
