package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.mistralai.api.MistralAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * MistralAi Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class MistralAiEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 默认模型名称：mistral-embed
     */
    private static final String DEFAULT_MODEL = MistralAiApi.EmbeddingModel.EMBED.getValue();

    /**
     * MistralAi密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认mistral-embed
     */
    private String model;

    public MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel embeddingModel, String apiKey) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey);
    }

    public MistralAiEmbeddingEntity(String model, String apiKey) {
        super(EmbeddingServiceType.MistralAI);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(MistralAiApi.EmbeddingModel embeddingModel) {
        this.model = embeddingModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 983485994833165478L;

}
