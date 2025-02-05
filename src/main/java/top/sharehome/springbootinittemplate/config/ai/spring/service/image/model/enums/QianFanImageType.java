package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * QianFan Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum QianFanImageType {

    SD_XL_768_768("sd_xl", 768, 768),

    SD_XL_1024_1024("sd_xl", 1024, 1024),

    SD_XL_1536_1536("sd_xl", 1536, 1536),

    SD_XL_2048_2048("sd_xl", 2048, 2048),

    SD_XL_1024_768("sd_xl", 1024, 768),

    SD_XL_2048_1536("sd_xl", 2048, 1536),

    SD_XL_768_1024("sd_xl", 768, 1024),

    SD_XL_1536_2048("sd_xl", 1536, 2048),

    SD_XL_1024_576("sd_xl", 1024, 576),

    SD_XL_2048_1152("sd_xl", 2048, 1152),

    SD_XL_576_1024("sd_xl", 576, 1024),

    SD_XL_1152_2048("sd_xl", 1152, 2048);

    /**
     * QianFan图像模型
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
