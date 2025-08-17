package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TranscriptionServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.AzureOpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.LanguageType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * AzureOpenAI Transcription模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class AzureOpenAiTranscriptionEntity extends TranscriptionModelBase implements Serializable {

    /**
     * AzureOpenAI密钥
     */
    private String apiKey;

    /**
     * AzureOpenAI语音转文字类型
     */
    private AzureOpenAiTranscriptionType azureOpenAiTranscriptionType;

    /**
     * AzureOpenAI服务端点
     */
    private String endpoint;

    /**
     * 语言（语音的语言，以提高准确性和效率）
     */
    private LanguageType language;

    /**
     * 温度（0-1之间，为0则表示自动）
     */
    private Float temperature;

    /**
     * 响应格式（默认JSON）
     */
    private AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format;

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, null, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, null, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String endpoint) {
        this(azureOpenAiTranscriptionType, apiKey, null, endpoint, null, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String endpoint, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, endpoint, null, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, null, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, null, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, null, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, null, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, null, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, null, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, LanguageType language) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, language, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, LanguageType language, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, language, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, LanguageType language) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, language, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, LanguageType language, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, language, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, LanguageType language, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, language, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, LanguageType language, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, null, language, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, LanguageType language, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, language, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, LanguageType language, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, null, language, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, null, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, null, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String baseUrl, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, format, baseUrl, null, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String baseUrl, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, baseUrl, null, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, LanguageType language) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, language, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, LanguageType language, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, language, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String baseUrl, LanguageType language) {
        this(azureOpenAiTranscriptionType, apiKey, format, baseUrl, language, null, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String baseUrl, LanguageType language, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, format, baseUrl, language, null, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, LanguageType language, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, language, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, String baseUrl, LanguageType language, Float temperature, Long readTimeout) {
        this(azureOpenAiTranscriptionType, apiKey, null, baseUrl, language, temperature, readTimeout);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String endpoint, LanguageType language, Float temperature) {
        this(azureOpenAiTranscriptionType, apiKey, format, endpoint, language, temperature, null);
    }

    public AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType azureOpenAiTranscriptionType, String apiKey, AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat format, String endpoint, LanguageType language, Float temperature, Long readTimeout) {
        super(TranscriptionServiceType.AzureOpenAI, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(azureOpenAiTranscriptionType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[azureOpenAiTranscriptionType]不能为空");
        }
        if (StringUtils.isBlank(endpoint)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[endpoint]不能为空");
        }
        this.azureOpenAiTranscriptionType = azureOpenAiTranscriptionType;
        this.apiKey = apiKey;
        this.format = Objects.isNull(format) ? AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat.JSON : format;
        this.endpoint = endpoint;
        this.language = language;
        this.temperature = Objects.isNull(temperature) || temperature < 0 || temperature > 0 ? 0f : temperature;
    }

    @Override
    public String getModel() {
        return azureOpenAiTranscriptionType.getModel();
    }

    @Serial
    private static final long serialVersionUID = 3190604993674221440L;
}
