package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.ZhiPuAiImageType;
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
     * ZhiPuAI图像类型
     */
    private ZhiPuAiImageType zhiPuAiImageType;

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    /**
     * 用户标识
     */
    private String user;

    public ZhiPuAiImageEntity(ZhiPuAiImageType zhiPuAiImageType, String apiKey) {
        this(zhiPuAiImageType, apiKey, null);
    }

    public ZhiPuAiImageEntity(ZhiPuAiImageType zhiPuAiImageType, String apiKey, String user) {
        super(ImageServiceType.ZhiPuAI, null);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (Objects.isNull(zhiPuAiImageType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[zhiPuAiImageType]不能为空");
        }
        this.apiKey = apiKey;
        this.zhiPuAiImageType = zhiPuAiImageType;
        this.user = user;
    }

    @Override
    public String getModel() {
        return zhiPuAiImageType.getModel();
    }

    @Serial
    private static final long serialVersionUID = -4023461810627194329L;
}
