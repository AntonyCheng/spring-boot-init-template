package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.mistralai.api.MistralAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * MistralAi Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class MistralAiChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：ministral-8b-latest
     */
    public static final String DEFAULT_MODEL = MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getValue();

    /**
     * MistralAi密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认ministral-8b-latest
     */
    private String model;

    /**
     * 最大响应Token数
     */
    private Integer maxTokens;

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, null);
    }

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey, Integer maxTokens) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, maxTokens);
    }

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, null);
    }

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP, Integer maxTokens) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, maxTokens);
    }

    public MistralAiChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null, null);
    }

    public MistralAiChatEntity(String model, String apiKey, Integer maxTokens) {
        this(model, apiKey, null, null, maxTokens);
    }

    public MistralAiChatEntity(String model, String apiKey, Double temperature, Double topP) {
        this(model, apiKey, temperature, topP, null);
    }

    public MistralAiChatEntity(String model, String apiKey, Double temperature, Double topP, Integer maxTokens) {
        super(ChatServiceType.MistralAI, temperature, topP, null);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
        this.maxTokens = maxTokens;
    }

    public void setName(MistralAiApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -4982739562800889455L;

}
