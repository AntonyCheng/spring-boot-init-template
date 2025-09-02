package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final String model;

    /**
     * 图像宽度
     */
    private final Integer width;

    /**
     * 图像高度
     */
    private final Integer height;

    public static AzureOpenAiImageType getTypeByName(String name) {
        List<AzureOpenAiImageType> list = Arrays.stream(AzureOpenAiImageType.values()).filter(azureOpenAiImageType -> Objects.equals(azureOpenAiImageType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
