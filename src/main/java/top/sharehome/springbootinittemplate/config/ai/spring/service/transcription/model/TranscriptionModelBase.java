package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TranscriptionServiceType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * Transcription模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public abstract class TranscriptionModelBase {

    /**
     * 默认模型温度：0.8
     */
    public static final Double DEFAULT_TEMPERATURE = 0.0;

    /**
     * Transcription模型服务方
     */
    protected TranscriptionServiceType transcriptionServiceType;

    /**
     * 温度（0-1之间，为0则表示自动）
     */
    protected Double temperature;

    /**
     * 模型响应超时时间
     */
    @Setter
    protected Long readTimeout;

    public TranscriptionModelBase(TranscriptionServiceType transcriptionServiceType, Double temperature, Long readTimeout) {
        if (Objects.isNull(transcriptionServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[transcriptionServiceType]不能为空");
        }
        this.transcriptionServiceType = transcriptionServiceType;
        this.temperature = Objects.isNull(temperature) || temperature < 0 || temperature > 1 ? 0f : temperature;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? 3 * 60 * 1000 : readTimeout;
    }

    public abstract String getModel();

}
