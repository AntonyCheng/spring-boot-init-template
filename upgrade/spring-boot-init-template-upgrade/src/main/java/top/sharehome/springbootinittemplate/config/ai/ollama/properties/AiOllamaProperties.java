package top.sharehome.springbootinittemplate.config.ai.ollama.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI Ollama配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "ai.ollama")
public class AiOllamaProperties {

    /**
     * 是否启动
     */
    private Boolean enable = false;

    /**
     * Ollama地址
     */
    private String url = "127.0.0.1:11434";

    /**
     * 预训练大语言模型名称
     */
    private String modelName = "qwen:1.8b-chat-q8_0";

    /**
     * 模型温度（0<=x<=1，温度值越高，生成内容越有创意）
     */
    private Float temperature = 0.8f;

    /**
     * 模型Top-K（0<=x<=100，减少产生无意义的概率，Top-K值越高（例如100），越会给出更多样化/激进的答案）
     */
    private Integer topK = 50;

    /**
     * 模型Top-P（0<=x<=1，与Top-K搭配使用，Top-P值越高（例如0.95），越会导致更多样化/激进的文本）
     */
    private Float topP = 0.8f;

    /**
     * 模型重复惩罚（0<=x<=1.5，值越高，重复惩罚越严重）
     */
    private Float repeatPenalty = 1.1f;

}
