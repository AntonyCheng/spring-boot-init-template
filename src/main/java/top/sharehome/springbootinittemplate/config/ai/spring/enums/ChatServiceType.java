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
     * DeepSeek服务
     */
    DeepSeek("DeepSeek", "deepseek"),

    /**
     * OpenAI服务
     */
    OpenAI("OpenAI", "openai"),

    /**
     * Ollama服务
     */
    Ollama("Ollama", "ollama"),

    /**
     * ZhiPuAi服务
     */
    ZhiPuAI("智谱AI", "zhipu"),

    /**
     * MistralAi服务
     */
    MistralAI("MistralAI", "mistralai"),

    /**
     * MiniMax服务
     */
    MiniMax("MiniMax", "minimax"),

    /**
     * AzureOpenAI服务
     */
    AzureOpenAI("AzureOpenAI", "azureopenai");

    /**
     * 模型中文标签
     */
    private final String label;

    /**
     * 模型英文名称
     */
    private final String value;

}
