package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.stabilityai.StyleEnum;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ImageServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.QianFanImageType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * QianFan Image模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class QianFanImageEntity extends ImageModelBase implements Serializable {

    /**
     * QianFan公钥
     */
    private String apiKey;

    /**
     * QianFan私钥
     */
    private String secretKey;

    /**
     * QianFan图像类型
     */
    private QianFanImageType qianFanImageType;

    /**
     * 生成图像张数
     */
    private Integer n;

    /**
     * 生成图像风格
     */
    private StyleEnum styleEnum;

    /**
     * 用户标识
     */
    private String user;

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey) {
        this(qianFanImageType, apiKey, secretKey, null, null, null);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, String user) {
        this(qianFanImageType, apiKey, secretKey, null, null, user);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, StyleEnum styleEnum) {
        this(qianFanImageType, apiKey, secretKey, null, styleEnum, null);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, StyleEnum styleEnum, String user) {
        this(qianFanImageType, apiKey, secretKey, null, styleEnum, user);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, Integer n) {
        this(qianFanImageType, apiKey, secretKey, n, null, null);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, Integer n, String user) {
        this(qianFanImageType, apiKey, secretKey, n, null, user);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, Integer n, StyleEnum styleEnum) {
        this(qianFanImageType, apiKey, secretKey, n, styleEnum, null);
    }

    public QianFanImageEntity(QianFanImageType qianFanImageType, String apiKey, String secretKey, Integer n, StyleEnum styleEnum, String user) {
        super(ImageServiceType.QianFan);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (StringUtils.isBlank(secretKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[secretKey]不能为空");
        }
        if (Objects.isNull(qianFanImageType)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[qianFanImageType]不能为空");
        }
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.qianFanImageType = qianFanImageType;
        this.n = Objects.isNull(n) || n < 1 || n > 4 ? 1 : n;
        this.styleEnum = styleEnum;
        this.user = user;
    }

    @Serial
    private static final long serialVersionUID = -3686561056700913643L;

}
