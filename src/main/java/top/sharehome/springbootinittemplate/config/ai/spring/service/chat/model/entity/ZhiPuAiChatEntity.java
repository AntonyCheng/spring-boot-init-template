package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * ZhiPuAI Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ZhiPuAiChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：glm-4-flash
     */
    private static final String DEFAULT_MODEL = ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue();

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认glm-4-flash
     */
    private String model;

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, null);
    }

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey, Integer readTimeout) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, null, null, readTimeout);
    }

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, null);
    }

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP, Integer readTimeout) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, temperature, topP, readTimeout);
    }

    public ZhiPuAiChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null, null);
    }

    public ZhiPuAiChatEntity(String model, String apiKey, Integer readTimeout) {
        this(model, apiKey, null, null, readTimeout);
    }

    public ZhiPuAiChatEntity(String model, String apiKey, Double temperature, Double topP) {
        this(model, apiKey, temperature, topP, null);
    }

    public ZhiPuAiChatEntity(String model, String apiKey, Double temperature, Double topP, Integer readTimeout) {
        super(ChatServiceType.ZhiPuAI, temperature, topP, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(ZhiPuAiApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 7458291929332472829L;

}
