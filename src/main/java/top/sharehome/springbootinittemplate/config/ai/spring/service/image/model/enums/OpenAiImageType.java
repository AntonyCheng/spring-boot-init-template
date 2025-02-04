package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.ai.openai.api.OpenAiImageApi;

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
    DallE2_256_256(OpenAiImageApi.ImageModel.DALL_E_2, 256, 256, "standard"),

    /**
     * DALL·E 2 512×512
     */
    DallE2_512_512(OpenAiImageApi.ImageModel.DALL_E_2, 512, 512, "standard"),

    /**
     * DALL·E 2 1024×1024
     */
    DallE2_1024_1024(OpenAiImageApi.ImageModel.DALL_E_2, 1024, 1024, "standard"),

    /**
     * DALL·E 3 1024×1024
     */
    DallE3_1024_1024(OpenAiImageApi.ImageModel.DALL_E_3, 1024, 1024, "standard"),

    /**
     * DALL·E 3 1024×1792
     */
    DallE3_1024_1792(OpenAiImageApi.ImageModel.DALL_E_3, 1024, 1792, "standard"),

    /**
     * DALL·E 3 1792×1024
     */
    DallE3_1792_1024(OpenAiImageApi.ImageModel.DALL_E_3, 1792, 1024, "standard"),

    /**
     * DALL·E 3 1024×1024 高分辨率
     */
    DallE3_1024_1024_HD(OpenAiImageApi.ImageModel.DALL_E_3, 1024, 1024, "hd"),

    /**
     * DALL·E 3 1024×1792 高分辨率
     */
    DallE3_1024_1792_HD(OpenAiImageApi.ImageModel.DALL_E_3, 1024, 1792, "hd"),

    /**
     * DALL·E 3 1792×1024 高分辨率
     */
    DallE3_1792_1024_HD(OpenAiImageApi.ImageModel.DALL_E_3, 1792, 1024, "hd");

    /**
     * Open图像模型
     */
    private final OpenAiImageApi.ImageModel imageModel;

    /**
     * 图像高度
     */
    private final Integer height;

    /**
     * 图像宽度
     */
    private final Integer width;

    /**
     * 图像分辨率
     */
    private final String quality;

}
