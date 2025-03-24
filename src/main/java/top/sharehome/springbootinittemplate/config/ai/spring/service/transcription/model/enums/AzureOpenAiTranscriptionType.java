package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AzureOpenAI Transcription类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AzureOpenAiTranscriptionType {

    /**
     * Whisper
     */
    Whisper("whisper");

    /**
     * AzureOpenAI语音转文字模型
     */
    private final String transcriptionModel;

}
