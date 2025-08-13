package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.EmbeddingServiceType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * Embedding模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public abstract class EmbeddingModelBase {

    /**
     * Embedding模型服务方
     */
    protected EmbeddingServiceType embeddingServiceType;

    /**
     * 模型响应超时时间
     */
    @Setter
    protected Long readTimeout;

    public EmbeddingModelBase(EmbeddingServiceType embeddingServiceType, Long readTimeout) {
        if (Objects.isNull(embeddingServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[embeddingServiceType]不能为空");
        }
        this.embeddingServiceType = embeddingServiceType;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? 3 * 60 * 1000 : readTimeout;
    }

}
