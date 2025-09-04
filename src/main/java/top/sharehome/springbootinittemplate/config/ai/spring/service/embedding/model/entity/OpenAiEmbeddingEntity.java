package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * OpenAI Embedding模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OpenAiEmbeddingEntity extends EmbeddingModelBase implements Serializable {

    /**
     * 默认模型名称：text-embedding-3-small
     */
    public static final String DEFAULT_MODEL = OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_3_SMALL.getValue();

    /**
     * 默认OpenAI API接口地址
     */
    public static final String DEFAULT_BASE_URL = "https://api.openai.com";

    /**
     * OpenAI密钥
     */
    private String apiKey;

    /**
     * 模型名称，默认text-embedding-3-small
     */
    private String model;

    /**
     * OpenAI服务URL
     */
    private String baseUrl;

    public OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel embeddingModel, String apiKey) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey, null, null);
    }

    public OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel embeddingModel, String apiKey, Long readTimeout) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey, null, readTimeout);
    }

    public OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel embeddingModel, String apiKey, String baseUrl) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey, baseUrl, null);
    }

    public OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel embeddingModel, String apiKey, String baseUrl, Long readTimeout) {
        this(Objects.isNull(embeddingModel) ? DEFAULT_MODEL : embeddingModel.getValue(), apiKey, baseUrl, readTimeout);
    }

    public OpenAiEmbeddingEntity(String model, String apiKey) {
        this(model, apiKey, null, null);
    }

    public OpenAiEmbeddingEntity(String model, String apiKey, Long readTimeout) {
        this(model, apiKey, null, readTimeout);
    }

    public OpenAiEmbeddingEntity(String model, String apiKey, String baseUrl) {
        this(model, apiKey, baseUrl, null);
    }

    public OpenAiEmbeddingEntity(String model, String apiKey, String baseUrl, Long readTimeout) {
        super(EmbeddingServiceType.OpenAI, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL : baseUrl;
    }

    public void setName(OpenAiApi.EmbeddingModel embeddingModel) {
        this.model = embeddingModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 1570235925740102317L;

}
