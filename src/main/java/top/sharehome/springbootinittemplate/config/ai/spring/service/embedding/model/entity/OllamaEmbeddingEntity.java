package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ollama Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OllamaEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 模型名称
     */
    private String model;

    /**
     * Ollama服务URL
     */
    private String baseUrl;

    public OllamaEmbeddingEntity(String model, String baseUrl) {
        this(model, baseUrl, null);
    }

    public OllamaEmbeddingEntity(String model, String baseUrl, Long readTimeout) {
        super(EmbeddingServiceType.Ollama, readTimeout);
        if (StringUtils.isBlank(model)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]不能为空");
        }
        if (StringUtils.isBlank(baseUrl)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[baseUrl]不能为空");
        }
        this.model = model;
        this.baseUrl = baseUrl;
    }

    @Serial
    private static final long serialVersionUID = -6910082000680955412L;

}
