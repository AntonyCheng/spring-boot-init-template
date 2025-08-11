package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * Chat模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public abstract class ChatModelBase {

    /**
     * Chat模型服务方
     */
    protected ChatServiceType chatServiceType;

    /**
     * 模型温度，默认0.8
     */
    @Setter
    protected Double temperature;

    /**
     * 模型top-p，默认0.9
     */
    @Setter
    protected Double topP;

    /**
     * 模型响应超时时间
     */
    @Setter
    protected Integer readTimeout;

    public ChatModelBase(ChatServiceType chatServiceType, Double temperature, Double topP, Integer readTimeout) {
        if (Objects.isNull(chatServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatServiceType]不能为空");
        }
        this.chatServiceType = chatServiceType;
        this.temperature = Objects.isNull(temperature) ? 0.8 : temperature;
        this.topP = Objects.isNull(topP) ? 0.9 : temperature;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? 3 * 60 * 1000 : readTimeout;
    }

}
