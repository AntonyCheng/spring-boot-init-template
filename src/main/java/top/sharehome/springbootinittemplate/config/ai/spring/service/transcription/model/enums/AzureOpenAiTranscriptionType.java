package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final String model;

    public static AzureOpenAiTranscriptionType getTypeByName(String name) {
        List<AzureOpenAiTranscriptionType> list = Arrays.stream(AzureOpenAiTranscriptionType.values()).filter(azureOpenAiTranscriptionType -> Objects.equals(azureOpenAiTranscriptionType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
