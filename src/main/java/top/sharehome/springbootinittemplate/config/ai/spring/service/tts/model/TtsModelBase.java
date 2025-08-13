package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TranscriptionServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.TtsServiceType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * TTS模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public abstract class TtsModelBase {

    /**
     * TTS模型服务方
     */
    protected TtsServiceType ttsServiceType;

    /**
     * 模型响应超时时间
     */
    @Setter
    protected Long readTimeout;

    public TtsModelBase(TtsServiceType ttsServiceType, Long readTimeout) {
        if (Objects.isNull(ttsServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[ttsServiceType]不能为空");
        }
        this.ttsServiceType = ttsServiceType;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? 3 * 60 * 1000 : readTimeout;
    }

}
