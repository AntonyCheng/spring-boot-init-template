package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OpenAI TTS类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OpenAiTtsType {

    /**
     * TTS
     */
    TTS("tts-1"),

    /**
     * TTS HD
     */
    TTS_HD("tts-1-hd");

    /**
     * OpenAI文字转语音模型
     */
    private final String model;

    public static OpenAiTtsType getTypeByName(String name) {
        List<OpenAiTtsType> list = Arrays.stream(OpenAiTtsType.values()).filter(openAiTtsType -> Objects.equals(openAiTtsType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
