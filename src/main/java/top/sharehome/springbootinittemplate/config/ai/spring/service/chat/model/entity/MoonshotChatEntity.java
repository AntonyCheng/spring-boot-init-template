package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.moonshot.api.MoonshotApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Moonshot Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class MoonshotChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：moonshot-v1-8k
     */
    private static final String DEFAULT_MODEL = MoonshotApi.ChatModel.MOONSHOT_V1_8K.getName();

    /**
     * 模型名称，默认moonshot-v1-8k
     */
    private String model;

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    public MoonshotChatEntity(MoonshotApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, null, null);
    }

    public MoonshotChatEntity(MoonshotApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, temperature, topP);
    }

    public MoonshotChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null);
    }

    public MoonshotChatEntity(String model, String apiKey, Double temperature, Double topP) {
        super(ChatServiceType.Moonshot, temperature, topP);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(MoonshotApi.ChatModel chatModel) {
        this.model = chatModel.getName();
    }

    @Serial
    private static final long serialVersionUID = -2926134073130354042L;

}
