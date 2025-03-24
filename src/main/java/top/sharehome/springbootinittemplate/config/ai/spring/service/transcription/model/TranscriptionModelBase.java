package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
     * Transcription模型服务方
     */
    protected TranscriptionServiceType transcriptionServiceType;

    public TranscriptionModelBase(TranscriptionServiceType transcriptionServiceType) {
        if (Objects.isNull(transcriptionServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[transcriptionServiceType]不能为空");
        }
        this.transcriptionServiceType = transcriptionServiceType;
    }

}
