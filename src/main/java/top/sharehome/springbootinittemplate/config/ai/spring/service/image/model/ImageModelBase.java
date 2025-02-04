package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public ImageModelBase(ImageServiceType imageServiceType) {
        if (Objects.isNull(imageServiceType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatService]不能为空");
        }
        this.imageServiceType = imageServiceType;
    }

}
