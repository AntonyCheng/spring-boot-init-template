package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.stabilityai.StyleEnum;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.StabilityAiImageType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * StabilityAI Image模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class StabilityAiImageEntity extends ImageModelBase implements Serializable {

    /**
     * 默认StabilityAI API接口地址
     */
    private static final String DEFAULT_BASE_URL = "https://api.stability.ai";

    /**
     * 默认StabilityAI API接口URI地址
     */
    private static final String DEFAULT_BASE_URI = "/v1";

    /**
     * StabilityAI密钥
     */
    private String apiKey;

    /**
     * StabilityAI图像类型
     */
    private StabilityAiImageType stabilityAiImageType;

    /**
     * StabilityAI服务URL
     */
    private String baseUrl;

    /**
     * 生成图像张数
     */
    private Integer n;

    /**
     * 生成图像风格
     */
    private StyleEnum styleEnum;

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey) {
        this(stabilityAiImageType, apiKey, null, null, null, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, Long readTimeout) {
        this(stabilityAiImageType, apiKey, null, null, null, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, StyleEnum styleEnum) {
        this(stabilityAiImageType, apiKey, null, null, styleEnum, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, StyleEnum styleEnum, Long readTimeout) {
        this(stabilityAiImageType, apiKey, null, null, styleEnum, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, Integer n) {
        this(stabilityAiImageType, apiKey, null, n, null, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, Integer n, Long readTimeout) {
        this(stabilityAiImageType, apiKey, null, n, null, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, Integer n, StyleEnum styleEnum) {
        this(stabilityAiImageType, apiKey, null, n, styleEnum, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, Integer n, StyleEnum styleEnum, Long readTimeout) {
        this(stabilityAiImageType, apiKey, null, n, styleEnum, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl) {
        this(stabilityAiImageType, apiKey, baseUrl, null, null, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, Long readTimeout) {
        this(stabilityAiImageType, apiKey, baseUrl, null, null, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, StyleEnum styleEnum) {
        this(stabilityAiImageType, apiKey, baseUrl, null, styleEnum, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, StyleEnum styleEnum, Long readTimeout) {
        this(stabilityAiImageType, apiKey, baseUrl, null, styleEnum, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, Integer n) {
        this(stabilityAiImageType, apiKey, baseUrl, n, null, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, Integer n, Long readTimeout) {
        this(stabilityAiImageType, apiKey, baseUrl, n, null, readTimeout);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, Integer n, StyleEnum styleEnum) {
        this(stabilityAiImageType, apiKey, baseUrl, n, styleEnum, null);
    }

    public StabilityAiImageEntity(StabilityAiImageType stabilityAiImageType, String apiKey, String baseUrl, Integer n, StyleEnum styleEnum, Long readTimeout) {
        super(ImageServiceType.Stability, readTimeout);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(stabilityAiImageType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[stabilityAiImageType]不能为空");
        }
        this.apiKey = apiKey;
        this.stabilityAiImageType = stabilityAiImageType;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL + DEFAULT_BASE_URI : baseUrl + stabilityAiImageType.getBaseUri();
        this.n = Objects.isNull(n) || n < 1 || n > 4 ? 1 : n;
        this.styleEnum = styleEnum;
    }

    @Serial
    private static final long serialVersionUID = -6281265478943674439L;

}
