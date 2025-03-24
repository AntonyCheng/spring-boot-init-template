package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private final String ttsModel;

}
