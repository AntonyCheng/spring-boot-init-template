package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.api.OpenAiImageApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.AzureOpenAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.OpenAiImageType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * AzureOpenAI Image模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class AzureOpenAiImageEntity extends ImageModelBase implements Serializable {

    /**
     * AzureOpenAI密钥
     */
    private String apiKey;

    /**
     * AzureOpenAI图像类型
     */
    private AzureOpenAiImageType azureOpenAiImageType;

    /**
     * AzureOpenAI服务端点
     */
    private String endpoint;

    /**
     * 生成图像张数
     */
    private Integer n;


    public AzureOpenAiImageEntity(AzureOpenAiImageType azureOpenAiImageType, String apiKey, String endpoint) {
        this(azureOpenAiImageType, apiKey, endpoint, null);
    }

    public AzureOpenAiImageEntity(AzureOpenAiImageType azureOpenAiImageType, String apiKey, String endpoint, Integer n) {
        super(ImageServiceType.AzureOpenAi);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(azureOpenAiImageType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[azureOpenAiImageType]不能为空");
        }
        if (StringUtils.isBlank(endpoint)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[endpoint]不能为空");
        }
        this.apiKey = apiKey;
        this.azureOpenAiImageType = azureOpenAiImageType;
        this.endpoint = endpoint;
        this.n = OpenAiImageApi.ImageModel.DALL_E_3.getValue().equals(azureOpenAiImageType.getImageModel()) || Objects.isNull(n) || n < 1 || n > 10 ? 1 : n;
    }

    @Serial
    private static final long serialVersionUID = -9206031514959163500L;

}
