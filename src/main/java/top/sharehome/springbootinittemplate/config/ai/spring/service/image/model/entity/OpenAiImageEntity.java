package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiImageApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.OpenAiImageType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * OpenAI Image模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class OpenAiImageEntity extends ImageModelBase implements Serializable {

    /**
     * 默认OpenAI API接口地址
     */
    private static final String DEFAULT_BASE_URL = "https://api.openai.com";

    /**
     * OpenAI密钥
     */
    private String apiKey;

    /**
     * OpenAI图像类型
     */
    private OpenAiImageType openAiImageType;

    /**
     * OpenAI服务URL
     */
    private String baseUrl;

    /**
     * 生成图像张数
     */
    private Integer n;

    public OpenAiImageEntity(OpenAiImageType openAiImageType, String apiKey) {
        this(openAiImageType, apiKey, null, null);
    }

    public OpenAiImageEntity(OpenAiImageType openAiImageType, String apiKey, Integer n) {
        this(openAiImageType, apiKey, null, n);
    }

    public OpenAiImageEntity(OpenAiImageType openAiImageType, String apiKey, String baseUrl) {
        this(openAiImageType, apiKey, baseUrl, null);
    }

    public OpenAiImageEntity(OpenAiImageType openAiImageType, String apiKey, String baseUrl, Integer n) {
        super(ImageServiceType.OpenAi);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(openAiImageType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[openAiImageType]不能为空");
        }
        this.apiKey = apiKey;
        this.openAiImageType = openAiImageType;
        this.baseUrl = StringUtils.isBlank(baseUrl) ? DEFAULT_BASE_URL : baseUrl;
        this.n = OpenAiImageApi.ImageModel.DALL_E_3.getValue().equals(openAiImageType.getImageModel().getValue()) || Objects.isNull(n) || n < 1 || n > 10 ? 1 : n;
    }

    @Serial
    private static final long serialVersionUID = 5714491181503496584L;

}
