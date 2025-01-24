package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * ZhiPuAI Chat模型类
 *
 * @author AntonyCheng
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ZhiPuAiChatEntity extends ChatModelBase implements Serializable {

    /**
     * 默认模型名称：glm-4-flash
     */
    private static final String DEFAULT_MODEL = ZhiPuAiApi.ChatModel.GLM_4_Flash.getName();

    /**
     * 模型名称，默认glm-4-flash
     */
    private String model;

    /**
     * ZhiPuAI密钥
     */
    private String apiKey;

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, null, null);
    }

    public ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel chatModel, String apiKey, Double temperature, Double topP) {
        this(Objects.isNull(chatModel) ? DEFAULT_MODEL : chatModel.getName(), apiKey, temperature, topP);
    }

    public ZhiPuAiChatEntity(String model, String apiKey) {
        this(model, apiKey, null, null);
    }

    public ZhiPuAiChatEntity(String model, String apiKey, Double temperature, Double topP) {
        super(ChatServiceType.ZhiPuAi, temperature, topP);
        if (StringUtils.isBlank(apiKey)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[apiKey]不能为空");
        }
        this.apiKey = apiKey;
        this.model = StringUtils.isBlank(model) ? DEFAULT_MODEL : model;
    }

    public void setName(ZhiPuAiApi.ChatModel chatModel) {
        this.model = chatModel.getName();
    }

    @Serial
    private static final long serialVersionUID = 7458291929332472829L;

}
