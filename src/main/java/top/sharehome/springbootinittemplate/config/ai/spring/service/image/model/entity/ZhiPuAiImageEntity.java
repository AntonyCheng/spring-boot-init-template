package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * ZhiPuAI Image模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ZhiPuAiImageEntity extends ImageModelBase implements Serializable {

    /**
     * 默认模型名称：cogview-3
     */
    private static final String DEFAULT_MODEL = ZhiPuAiImageApi.ImageModel.CogView_3.getValue();

    /**
     * 模型名称，默认cogview-3
     */
    private String model;

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    /**
     * 用户标识
     */
    private String user;

    public ZhiPuAiImageEntity(ZhiPuAiImageApi.ImageModel imageModel, String apiKey) {
        this(Objects.isNull(imageModel) ? DEFAULT_MODEL : imageModel.getValue(), apiKey, null);
    }

    public ZhiPuAiImageEntity(ZhiPuAiImageApi.ImageModel imageModel, String apiKey, String user) {
        this(imageModel.getValue(), apiKey, user);
    }

    public ZhiPuAiImageEntity(String model, String apiKey) {
        this(model, apiKey, null);
    }

    public ZhiPuAiImageEntity(String model, String apiKey, String user) {
        super(ImageServiceType.ZhiPuAi);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.user = user;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(ZhiPuAiImageApi.ImageModel imageModel) {
        this.model = imageModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = -4023461810627194329L;

}
