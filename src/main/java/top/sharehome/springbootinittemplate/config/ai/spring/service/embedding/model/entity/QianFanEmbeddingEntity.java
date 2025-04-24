package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.qianfan.api.QianFanApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * QianFan Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class QianFanEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 默认模型名称：embedding-v1
     */
    private static final String DEFAULT_MODEL = QianFanApi.EmbeddingModel.EMBEDDING_V1.getValue();

    /**
     * QianFan公钥
     */
    private String apiKey;

    /**
     * QianFan私钥
     */
    private String secretKey;

    /**
     * 模型名称，默认embedding-v1
     */
    private String model;

    public QianFanEmbeddingEntity(QianFanApi.EmbeddingModel embeddingModel, String apiKey, String secretKey) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey, secretKey);
    }

    public QianFanEmbeddingEntity(String model, String apiKey, String secretKey) {
        super(EmbeddingServiceType.QianFan);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (StringUtils.isBlank(secretKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[secretKey]不能为空");
        }
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(QianFanApi.EmbeddingModel embeddingModel) {
        this.model = embeddingModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -4777276903707960732L;

}
