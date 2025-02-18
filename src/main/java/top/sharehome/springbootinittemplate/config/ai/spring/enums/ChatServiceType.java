package top.sharehome.springbootinittemplate.config.ai.spring.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SpringAI Chat服务类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ChatServiceType {

    /**
     * OpenAI服务
     */
    OpenAi("OpenAI", "openai"),

    /**
     * Ollama服务
     */
    Ollama("Ollama", "ollama"),

    /**
     * ZhiPuAi服务
     */
    ZhiPuAi("智谱AI", "zhipu"),

    /**
     * Moonshot服务
     */
    Moonshot("月之暗面", "moonshot"),

    /**
     * MistralAi服务
     */
    MistralAi("MistralAI", "mistralai"),

    /**
     * QianFan服务
     */
    QianFan("千帆AI", "qianfan"),

    /**
     * MiniMax服务
     */
    MiniMax("MiniMax", "minimax"),

    /**
     * AzureOpenAi服务
     */
    AzureOpenAi("AzureOpenAi", "azureopenai");

    /**
     * 模型中文标签
     */
    private final String label;

    /**
     * 模型英文名称
     */
    private final String value;

}
