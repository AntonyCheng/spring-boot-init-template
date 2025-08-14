package top.sharehome.springbootinittemplate.config.ai.spring.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SpringAI Image服务类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ImageServiceType {

    /**
     * OpenAI服务
     */
    OpenAI("OpenAI", "openai"),

    /**
     * AzureOpenAI服务
     */
    AzureOpenAI("AzureOpenAI", "azureopenai"),

    /**
     * ZhiPuAi服务
     */
    ZhiPuAI("智谱AI", "zhipu"),

    /**
     * Stability服务
     */
    Stability("Stability", "stability");

    /**
     * 模型中文标签
     */
    private final String label;

    /**
     * 模型英文名称
     */
    private final String value;

}
