package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
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
     * 默认模型名称：open-mistral-7b
     */
    private static final String DEFAULT_MODEL = MistralAiApi.ChatModel.OPEN_MISTRAL_7B.getName();

    /**
     * 模型名称，默认open-mistral-7b
     */
    private String model;

    /**
     * MistralAi密钥
     */
    private String apiKey;

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, null, null);
    }

    public MistralAiChatEntity(MistralAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, temperature, topP);
    }

    public MistralAiChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null);
    }

    public MistralAiChatEntity(String model, String apiKey, Double temperature, Double topP) {
        super(ChatServiceType.MistralAi, temperature, topP);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(MistralAiApi.ChatModel chatModel) {
        this.model = chatModel.getName();
    }

    @Serial
    private static final long serialVersionUID = -4982739562800889455L;

}
