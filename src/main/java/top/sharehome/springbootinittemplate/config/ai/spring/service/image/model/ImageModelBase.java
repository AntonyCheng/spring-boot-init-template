package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * Image模型基础属性类
 *
 * @author AntonyCheng
 */
@Getter
@NoArgsConstructor
public abstract class ImageModelBase {

    /**
     * Image模型服务方
     */
    protected ImageServiceType imageServiceType;

    /**
     * 模型响应超时时间
     */
    @Setter
    protected Long readTimeout;

    public ImageModelBase(ImageServiceType imageServiceType, Long readTimeout) {
        if (Objects.isNull(imageServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[imageServiceType]不能为空");
        }
        this.imageServiceType = imageServiceType;
        this.readTimeout = Objects.isNull(readTimeout) || readTimeout <= 0 ? 3 * 60 * 1000 : readTimeout;
    }

}
