package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OpenAI Transcription类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OpenAiTranscriptionType {

    /**
     * Whisper
     */
    Whisper("whisper-1");

    /**
     * OpenAI语音转文字模型
     */
    private final String transcriptionModel;

}
