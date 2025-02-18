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
    OpenAi("OpenAI", "openai"),

    /**
     * AzureOpenAi服务
     */
    AzureOpenAi("AzureOpenAi", "azureopenai"),

    /**
     * ZhiPuAi服务
     */
    ZhiPuAi("智谱AI", "zhipu"),

    /**
     * QianFan服务
     */
    QianFan("千帆AI", "qianfan"),

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
