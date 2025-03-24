package top.sharehome.springbootinittemplate.config.ai.spring.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SpringAI TTS服务类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum TtsServiceType {

    /**
     * OpenAi服务
     */
    OpenAI("OpenAI", "openai");

    /**
     * 模型中文标签
     */
    private final String label;

    /**
     * 模型英文名称
     */
    private final String value;

}
