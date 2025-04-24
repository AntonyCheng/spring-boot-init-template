package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * ZhiPuAI Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ZhiPuAiEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 默认模型名称：Embedding-2
     */
    private static final String DEFAULT_MODEL = ZhiPuAiApi.EmbeddingModel.Embedding_2.getValue();

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认Embedding-2
     */
    private String model;

    public ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel embeddingModel, String apiKey) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey);
    }

    public ZhiPuAiEmbeddingEntity(String model, String apiKey) {
        super(EmbeddingServiceType.ZhiPuAI);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(ZhiPuAiApi.EmbeddingModel embeddingModel) {
        this.model = embeddingModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 1764442969362131181L;

}
