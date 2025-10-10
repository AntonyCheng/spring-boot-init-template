package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;

/**
 * DeepSeek Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class DeepSeekChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：deepseek-r1
     */
    public static final String DEFAULT_MODEL = "deepseek-r1";

    /**
     * 默认DeepSeek API接口地址
     */
    public static final String DEFAULT_BASE_URL = "https://api.deepseek.com";

    /**
     * 默认DeepSeek密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认deepseek-r1
     */
    private String model;

    /**
     * DeepSeek服务URL
     */
    private String baseUrl;

    /**
     * 最大响应Token数
     */
    private Integer maxTokens;

    public DeepSeekChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null, null, null, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, Integer maxTokens) {
        this(model, apiKey, null, null, null, null, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, Long readTimeout) {
        this(model, apiKey, null, null, null, readTimeout, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, Long readTimeout, Integer maxTokens) {
        this(model, apiKey, null, null, null, readTimeout, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, Double temperature, Double topP) {
        this(model, apiKey, null, temperature, topP, null, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, Double temperature, Double topP, Integer maxTokens) {
        this(model, apiKey, null, temperature, topP, null, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, Double temperature, Double topP, Long readTimeout) {
        this(model, apiKey, null, temperature, topP, readTimeout, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, Double temperature, Double topP, Long readTimeout, Integer maxTokens) {
        this(model, apiKey, null, temperature, topP, readTimeout, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl) {
        this(model, apiKey, baseUrl, null, null, null, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Integer maxTokens) {
        this(model, apiKey, baseUrl, null, null, null, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Long readTimeout) {
        this(model, apiKey, baseUrl, null, null, readTimeout, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Long readTimeout, Integer maxTokens) {
        this(model, apiKey, baseUrl, null, null, readTimeout, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Double temperature, Double topP) {
        this(model, apiKey, baseUrl, temperature, topP, null, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Double temperature, Double topP, Integer maxTokens) {
        this(model, apiKey, baseUrl, temperature, topP, null, maxTokens);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Double temperature, Double topP, Long readTimeout) {
        this(DEFAULT_MODEL, apiKey, baseUrl, temperature, topP, readTimeout, null);
    }

    public DeepSeekChatEntity(String model, String apiKey, String baseUrl, Double temperature, Double topP, Long readTimeout, Integer maxTokens) {
        super(ChatServiceType.DeepSeek, temperature, topP, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL : baseUrl;
        this.maxTokens = maxTokens;
    }

    public void setName(OpenAiApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -4381411999861320112L;

}
