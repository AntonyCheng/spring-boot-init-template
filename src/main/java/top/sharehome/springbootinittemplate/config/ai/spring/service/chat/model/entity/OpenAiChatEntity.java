package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.openai.api.OpenAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * OpenAI Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OpenAiChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：gpt-3.5-turbo
     */
    private static final String DEFAULT_MODEL = OpenAiApi.ChatModel.GPT_3_5_TURBO.getValue();

    /**
     * 模型名称，默认gpt-3.5-turbo
     */
    private String model;

    /**
     * OpenAI服务URL
     */
    private String baseUrl;

    /**
     * OpenAI密钥
     */
    private String apiKey;

    public OpenAiChatEntity(OpenAiApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, null);
    }

    public OpenAiChatEntity(OpenAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, temperature, topP);
    }

    public OpenAiChatEntity(OpenAiApi.ChatModel chatModel, String apiKey, String baseUrl) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, baseUrl, null, null);
    }

    public OpenAiChatEntity(OpenAiApi.ChatModel chatModel, String apiKey, String baseUrl, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, baseUrl, temperature, topP);
    }

    public OpenAiChatEntity(String name, String apiKey) {
        this(name, apiKey, null, null, null);
    }

    public OpenAiChatEntity(String name, String apiKey, String baseUrl) {
        this(name, apiKey, baseUrl, null, null);
    }

    public OpenAiChatEntity(String model, String apiKey, String baseUrl, Double temperature, Double topP) {
        super(ChatService.OpenAi, temperature, topP);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
        this.baseUrl = Objects.isNull(baseUrl) ? "" : baseUrl;
    }

    public void setName(OpenAiApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -20796799230618718L;

}
