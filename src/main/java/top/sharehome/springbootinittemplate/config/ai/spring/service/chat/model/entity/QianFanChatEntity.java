package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.qianfan.api.QianFanApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * QianFan Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class QianFanChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：ernie_speed
     */
    private static final String DEFAULT_MODEL = QianFanApi.ChatModel.ERNIE_Speed_8K.getValue();

    /**
     * 模型名称，默认ernie_speed
     */
    private String model;

    /**
     * QianFan公钥
     */
    private String apiKey;

    /**
     * QianFan私钥
     */
    private String secretKey;

    public QianFanChatEntity(QianFanApi.ChatModel chatModel, String apiKey, String secretKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, secretKey, null, null);
    }

    public QianFanChatEntity(QianFanApi.ChatModel chatModel, String apiKey, String secretKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getValue(), apiKey, secretKey, temperature, topP);
    }

    public QianFanChatEntity(String model, String apiKey, String secretKey) {
        this(model, apiKey, secretKey, null, null);
    }

    public QianFanChatEntity(String model, String apiKey, String secretKey, Double temperature, Double topP) {
        super(ChatServiceType.QianFan, temperature, topP);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        if (StringUtils.isBlank(secretKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[secretKey]不能为空");
        }
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(QianFanApi.ChatModel chatModel) {
        this.model = chatModel.getValue();
    }

    @Serial
    private static final long serialVersionUID = 657021298325517595L;

}
