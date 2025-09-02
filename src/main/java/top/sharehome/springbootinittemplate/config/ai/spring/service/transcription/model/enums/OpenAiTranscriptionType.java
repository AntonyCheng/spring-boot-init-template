package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final String model;

    public static OpenAiTranscriptionType getTypeByName(String name) {
        List<OpenAiTranscriptionType> list = Arrays.stream(OpenAiTranscriptionType.values()).filter(openAiTranscriptionType -> Objects.equals(openAiTranscriptionType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
