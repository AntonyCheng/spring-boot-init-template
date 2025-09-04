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
     * 默认模型温度：0.8
     */
    public static final Double DEFAULT_TEMPERATURE = 0.8;

    /**
     * 默认模型top-p：0.9
     */
    public static final Double DEFAULT_TOP_P = 0.9;

    /**
     * 默认模型响应超时时间：3*60*1000毫秒
     */
    public static final Long DEFAULT_READ_TIMEOUT = 3 * 60 * 1000L;

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
     * 模型响应超时时间，默认3*60*1000毫秒
     */
    @Setter
    protected Long readTimeout;

    public ChatModelBase(ChatServiceType chatServiceType, Double temperature, Double topP, Long readTimeout) {
        if (Objects.isNull(chatServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatServiceType]不能为空");
        }
        this.chatServiceType = chatServiceType;
        this.temperature = Objects.isNull(temperature) ? DEFAULT_TEMPERATURE : temperature;
        this.topP = Objects.isNull(topP) ? DEFAULT_TOP_P : topP;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? DEFAULT_READ_TIMEOUT : readTimeout;
    }

    public abstract String getModel();

}
