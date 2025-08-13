package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TtsServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums.OpenAiTtsType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * OpenAI TTS模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OpenAiTtsEntity extends TtsModelBase implements Serializable {

    /**
     * 默认OpenAI API接口地址
     */
    private static final String DEFAULT_BASE_URL = "https://api.openai.com";

    /**
     * OpenAI密钥
     */
    private String apiKey;

    /**
     * OpenAI文字转语音类型
     */
    private OpenAiTtsType openAiTtsType;

    /**
     * OpenAI服务URL
     */
    private String baseUrl;

    /**
     * 音色（默认ALLOY）
     */
    private OpenAiAudioApi.SpeechRequest.Voice voice;

    /**
     * 响应格式（默认MP3）
     */
    private OpenAiAudioApi.SpeechRequest.AudioResponseFormat format;

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey) {
        this(openAiTtsType, apiKey, null, null, null, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, Long readTimeout) {
        this(openAiTtsType, apiKey, null, null, null, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format) {
        this(openAiTtsType, apiKey, format, null, null, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format, Long readTimeout) {
        this(openAiTtsType, apiKey, format, null, null, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, String baseUrl) {
        this(openAiTtsType, apiKey, null, baseUrl, null, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, String baseUrl, Long readTimeout) {
        this(openAiTtsType, apiKey, null, baseUrl, null, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format, String baseUrl) {
        this(openAiTtsType, apiKey, format, baseUrl, null, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format, String baseUrl, Long readTimeout) {
        this(openAiTtsType, apiKey, format, baseUrl, null, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.Voice voice) {
        this(openAiTtsType, apiKey, null, null, voice, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.Voice voice, Long readTimeout) {
        this(openAiTtsType, apiKey, null, null, voice, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, String baseUrl, OpenAiAudioApi.SpeechRequest.Voice voice) {
        this(openAiTtsType, apiKey, null, baseUrl, voice, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, String baseUrl, OpenAiAudioApi.SpeechRequest.Voice voice, Long readTimeout) {
        this(openAiTtsType, apiKey, null, baseUrl, voice, readTimeout);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format, String baseUrl, OpenAiAudioApi.SpeechRequest.Voice voice) {
        this(openAiTtsType, apiKey, format, baseUrl, voice, null);
    }

    public OpenAiTtsEntity(OpenAiTtsType openAiTtsType, String apiKey, OpenAiAudioApi.SpeechRequest.AudioResponseFormat format, String baseUrl, OpenAiAudioApi.SpeechRequest.Voice voice, Long readTimeout) {
        super(TtsServiceType.OpenAI, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(openAiTtsType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[openAiTtsType]不能为空");
        }
        this.openAiTtsType = openAiTtsType;
        this.apiKey = apiKey;
        this.format = Objects.isNull(format) ? OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3 : format;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL : baseUrl;
        this.voice = Objects.isNull(voice) ? OpenAiAudioApi.SpeechRequest.Voice.ALLOY : voice;
    }

    @Serial
    private static final long serialVersionUID = -7448156234178317721L;

}
