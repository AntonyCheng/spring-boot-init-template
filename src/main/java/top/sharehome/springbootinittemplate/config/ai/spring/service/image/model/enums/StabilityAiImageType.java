package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * StabilityAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum StabilityAiImageType {

    SDXL_1_0_1024_1024("/v1", "stable-diffusion-xl-1024-v1-0", 1024, 1024),

    SDXL_1_0_1152_896("/v1", "stable-diffusion-xl-1024-v1-0", 1152, 896),

    SDXL_1_0_896_1152("/v1", "stable-diffusion-xl-1024-v1-0", 896, 1152),

    SDXL_1_0_1216_832("/v1", "stable-diffusion-xl-1024-v1-0", 1216, 832),

    SDXL_1_0_1344_768("/v1", "stable-diffusion-xl-1024-v1-0", 1344, 768),

    SDXL_1_0_768_1344("/v1", "stable-diffusion-xl-1024-v1-0", 768, 1344),

    SDXL_1_0_1536_640("/v1", "stable-diffusion-xl-1024-v1-0", 1536, 640),

    SDXL_1_0_640_1536("/v1", "stable-diffusion-xl-1024-v1-0", 640, 1536),

    SD_1_6_1024_1024("/v1", "stable-diffusion-v1-6", 1024, 1024),

    SD_1_6_1152_896("/v1", "stable-diffusion-v1-6", 1152, 896),

    SD_1_6_896_1152("/v1", "stable-diffusion-v1-6", 896, 1152),

    SD_1_6_1216_832("/v1", "stable-diffusion-v1-6", 1216, 832),

    SD_1_6_1344_768("/v1", "stable-diffusion-v1-6", 1344, 768),

    SD_1_6_768_1344("/v1", "stable-diffusion-v1-6", 768, 1344),

    SD_1_6_1536_640("/v1", "stable-diffusion-v1-6", 1536, 640),

    SD_1_6_640_1536("/v1", "stable-diffusion-v1-6", 640, 1536);

    /**
     * StabilityAI API接口URI地址
     */
    private final String baseUri;

    /**
     * StabilityAI图像模型
     */
    private final String imageModel;

    /**
     * 图像高度
     */
    private final Integer height;

    /**
     * 图像宽度
     */
    private final Integer width;

}
