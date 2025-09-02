package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OpenAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OpenAiImageType {

    /**
     * DALL·E 2 256×256
     */
    DallE2_256_256("dall-e-2", 256, 256, "standard"),

    /**
     * DALL·E 2 512×512
     */
    DallE2_512_512("dall-e-2", 512, 512, "standard"),

    /**
     * DALL·E 2 1024×1024
     */
    DallE2_1024_1024("dall-e-2", 1024, 1024, "standard"),

    /**
     * DALL·E 3 1024×1024
     */
    DallE3_1024_1024("dall-e-3", 1024, 1024, "standard"),

    /**
     * DALL·E 3 1024×1792
     */
    DallE3_1024_1792("dall-e-3", 1024, 1792, "standard"),

    /**
     * DALL·E 3 1792×1024
     */
    DallE3_1792_1024("dall-e-3", 1792, 1024, "standard"),

    /**
     * DALL·E 3 1024×1024 高分辨率
     */
    DallE3_1024_1024_HD("dall-e-3", 1024, 1024, "hd"),

    /**
     * DALL·E 3 1024×1792 高分辨率
     */
    DallE3_1024_1792_HD("dall-e-3", 1024, 1792, "hd"),

    /**
     * DALL·E 3 1792×1024 高分辨率
     */
    DallE3_1792_1024_HD("dall-e-3", 1792, 1024, "hd");

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

    /**
     * 图像分辨率
     */
    private final String quality;

    public static OpenAiImageType getTypeByName(String name) {
        List<OpenAiImageType> list = Arrays.stream(OpenAiImageType.values()).filter(openAiImageType -> Objects.equals(openAiImageType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
