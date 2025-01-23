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
public enum ChatService {

    /**
     * OpenAI Chat服务
     */
    OpenAi("OpenAI", "openai"),

    /**
     * Ollama Chat服务
     */
    Ollama("Ollama", "ollama"),

    /**
     * ZhiPuAi Chat服务
     */
    ZhiPuAi("智谱AI", "zhipu"),

    /**
     * Moonshot Chat服务
     */
    Moonshot("月之暗面", "moonshot"),

    /**
     * MistralAi Chat服务
     */
    MistralAi("MistralAI", "mistralai"),

    /**
     * QianFan Chat服务
     */
    QianFan("千帆AI", "qianfan"),


    /**
     * MiniMax Chat服务
     */
    MiniMax("MiniMax", "minimax");

    /**
     * 模型中文标签
     */
    private final String label;

    /**
     * 模型英文名称
     */
    private final String value;

}
