package top.sharehome.springbootinittemplate.config.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI服务
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AiService {

    /**
     * 智谱AI服务
     */
    ZhiPuAi(),

    /**
     * OllamaAI服务
     */
    OllamaAi()

}
