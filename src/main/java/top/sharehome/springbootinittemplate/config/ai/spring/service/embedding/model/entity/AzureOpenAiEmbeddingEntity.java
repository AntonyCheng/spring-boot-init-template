package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import com.azure.ai.openai.OpenAIServiceVersion;
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
import java.util.Objects;

/**
 * AzureOpenAI Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class AzureOpenAiEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * AzureOpenAI密钥
     */
    private String apiKey;

    /**
     * 模型部署名称
     */
    private String model;

    /**
     * 模型版本
     */
    private OpenAIServiceVersion modelVersion;

    /**
     * AzureOpenAI服务端点
     */
    private String endpoint;

    public AzureOpenAiEmbeddingEntity(String model, OpenAIServiceVersion modelVersion, String apiKey, String endpoint) {
        this(model, modelVersion, apiKey, endpoint, null);
    }

    public AzureOpenAiEmbeddingEntity(String model, OpenAIServiceVersion modelVersion, String apiKey, String endpoint, Long readTimeout) {
        super(EmbeddingServiceType.AzureOpenAI, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (StringUtils.isBlank(model)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]不能为空");
        }
        if (Objects.isNull(modelVersion)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[modelVersion]不能为空");
        }
        if (StringUtils.isBlank(endpoint)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[endpoint]不能为空");
        }
        this.apiKey = apiKey;
        this.model = model;
        this.modelVersion = modelVersion;
        this.endpoint = endpoint;
    }

    @Serial
    private static final long serialVersionUID = -6088923807726631706L;

}
