package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.minimax.api.MiniMaxApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * MiniMax Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class MiniMaxChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：abab5.5-chat
     */
    public static final String DEFAULT_MODEL = MiniMaxApi.ChatModel.ABAB_5_5_Chat.getValue();

    /**
     * MiniMax密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认abab5.5-chat
     */
    private String model;

    /**
     * 最大响应Token数
     */
    private Integer maxTokens;

    public MiniMaxChatEntity(MiniMaxApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, null);
    }

    public MiniMaxChatEntity(MiniMaxApi.ChatModel chatModel, String apiKey, Integer maxTokens) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, maxTokens);
    }

    public MiniMaxChatEntity(MiniMaxApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, null);
    }

    public MiniMaxChatEntity(MiniMaxApi.ChatModel chatModel, String apiKey, Double temperature, Double topP, Integer maxTokens) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, maxTokens);
    }

    public MiniMaxChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null, null);
    }

    public MiniMaxChatEntity(String model, String apiKey, Integer maxTokens) {
        this(model, apiKey, null, null, maxTokens);
    }

    public MiniMaxChatEntity(String model, String apiKey, Double temperature, Double topP) {
        this(DEFAULT_MODEL, apiKey, temperature, topP, null);
    }

    public MiniMaxChatEntity(String model, String apiKey, Double temperature, Double topP, Integer maxTokens) {
        super(ChatServiceType.MiniMax, temperature, topP, null);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
        this.maxTokens = maxTokens;
    }

    public void setName(MiniMaxApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -862209805779534569L;

}
