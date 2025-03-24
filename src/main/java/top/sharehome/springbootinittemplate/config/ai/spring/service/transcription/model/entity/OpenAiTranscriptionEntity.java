package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TranscriptionServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.LanguageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.OpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * OpenAI Transcription模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OpenAiTranscriptionEntity extends TranscriptionModelBase implements Serializable {

    /**
     * 默认OpenAI API接口地址
     */
    private static final String DEFAULT_BASE_URL = "https://api.openai.com";

    /**
     * OpenAI密钥
     */
    private String apiKey;

    /**
     * OpenAI语音转文字类型
     */
    private OpenAiTranscriptionType openAiTranscriptionType;

    /**
     * OpenAI服务URL
     */
    private String baseUrl;

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
    private OpenAiAudioApi.TranscriptResponseFormat format;

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey) {
        this(openAiTranscriptionType, apiKey, null, null, null, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format) {
        this(openAiTranscriptionType, apiKey, format, null, null, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, Float temperature) {
        this(openAiTranscriptionType, apiKey, null, null, null, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, Float temperature) {
        this(openAiTranscriptionType, apiKey, format, null, null, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, LanguageType language) {
        this(openAiTranscriptionType, apiKey, null, null, language, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, LanguageType language) {
        this(openAiTranscriptionType, apiKey, format, null, language, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, LanguageType language, Float temperature) {
        this(openAiTranscriptionType, apiKey, null, null, language, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, LanguageType language, Float temperature) {
        this(openAiTranscriptionType, apiKey, format, null, language, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, String baseUrl, Float temperature) {
        this(openAiTranscriptionType, apiKey, null, baseUrl, null, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, String baseUrl, Float temperature) {
        this(openAiTranscriptionType, apiKey, format, baseUrl, null, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, String baseUrl, LanguageType language) {
        this(openAiTranscriptionType, apiKey, null, baseUrl, language, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, String baseUrl, LanguageType language) {
        this(openAiTranscriptionType, apiKey, format, baseUrl, language, null);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, String baseUrl, LanguageType language, Float temperature) {
        this(openAiTranscriptionType, apiKey, null, baseUrl, language, temperature);
    }

    public OpenAiTranscriptionEntity(OpenAiTranscriptionType openAiTranscriptionType, String apiKey, OpenAiAudioApi.TranscriptResponseFormat format, String baseUrl, LanguageType language, Float temperature) {
        super(TranscriptionServiceType.OpenAI);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(openAiTranscriptionType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[openAiTranscriptionType]不能为空");
        }
        this.openAiTranscriptionType = openAiTranscriptionType;
        this.apiKey = apiKey;
        this.format = Objects.isNull(format) ? OpenAiAudioApi.TranscriptResponseFormat.JSON : format;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL : baseUrl;
        this.language = language;
        this.temperature = Objects.isNull(temperature) || temperature < 0 || temperature > 0 ? 0f : temperature;
    }

    @Serial
    private static final long serialVersionUID = -1432011716496457507L;

}
