package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.ollama.api.OllamaModel;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Ollama Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OllamaChatEntity extends ChatModelBase implements Serializable {

    /**
     * 模型名称
     */
    private String model;

    /**
     * Ollama服务URL
     */
    private String baseUrl;

    public OllamaChatEntity(OllamaModel chatModel, String baseUrl) {
        this(Objects.isNull(chatModel) ? "" : chatModel.getName(), baseUrl, null, null, null);
    }

    public OllamaChatEntity(OllamaModel chatModel, String baseUrl, Long readTimeout) {
        this(Objects.isNull(chatModel) ? "" : chatModel.getName(), baseUrl, null, null, readTimeout);
    }

    public OllamaChatEntity(OllamaModel chatModel, String baseUrl, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? "" : chatModel.getName(), baseUrl, temperature, topP, null);
    }

    public OllamaChatEntity(OllamaModel chatModel, String baseUrl, Double temperature, Double topP, Long readTimeout) {
        this(Objects.isNull(chatModel) ? "" : chatModel.getName(), baseUrl, temperature, topP, readTimeout);
    }

    public OllamaChatEntity(String model, String baseUrl) {
        this(model, baseUrl, null, null, null);
    }

    public OllamaChatEntity(String model, String baseUrl, Long readTimeout) {
        this(model, baseUrl, null, null, readTimeout);
    }

    public OllamaChatEntity(String model, String baseUrl, Double temperature, Double topP) {
        this(model, baseUrl, temperature, topP, null);
    }

    public OllamaChatEntity(String model, String baseUrl, Double temperature, Double topP, Long readTimeout) {
        super(ChatServiceType.Ollama, temperature, topP, readTimeout);
        if (StringUtils.isBlank(model)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]不能为空");
        }
        if (StringUtils.isBlank(baseUrl)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[baseUrl]不能为空");
        }
        this.model = model;
        this.baseUrl = baseUrl;
    }

    public void setName(OllamaModel chatModel) {
        this.model = chatModel.getName();
    }

    @Serial
    private static final long serialVersionUID = 6968909611726416878L;

}
