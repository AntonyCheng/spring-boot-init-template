package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AzureOpenAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AzureOpenAiImageType {

    /**
     * DALL·E 3 1024×1024
     */
    DallE3_1024_1024("dall-e-3", 1024, 1024),

    /**
     * DALL·E 3 1024×1792
     */
    DallE3_1024_1792("dall-e-3", 1024, 1792),

    /**
     * DALL·E 3 1792×1024
     */
    DallE3_1792_1024("dall-e-3", 1792, 1024);

    /**
     * OpenAI图像模型
     */
    private final String imageModel;

    /**
     * 图像宽度
     */
    private final Integer width;

    /**
     * 图像高度
     */
    private final Integer height;

}
