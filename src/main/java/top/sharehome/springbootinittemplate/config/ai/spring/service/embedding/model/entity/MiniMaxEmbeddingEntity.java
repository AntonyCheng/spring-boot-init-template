package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.minimax.api.MiniMaxApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * MiniMax Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class MiniMaxEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 默认模型名称：embo-01
     */
    private static final String DEFAULT_MODEL = MiniMaxApi.EmbeddingModel.Embo_01.getValue();

    /**
     * MiniMax密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认embo-01
     */
    private String model;

    public MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel embeddingModel, String apiKey) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey);
    }

    public MiniMaxEmbeddingEntity(String model, String apiKey) {
        super(EmbeddingServiceType.MiniMax, null);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(MiniMaxApi.EmbeddingModel embeddingModel) {
        this.model = embeddingModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 3804902614122350535L;

}
