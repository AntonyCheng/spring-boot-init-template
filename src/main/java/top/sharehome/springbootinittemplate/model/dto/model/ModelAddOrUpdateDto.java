package top.sharehome.springbootinittemplate.model.dto.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.GetGroup;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import java.io.Serial;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_MODEL_SERVICE_STR;
import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_MODEL_TYPE_STR;

/**
 * 管理员添加模型Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelAddOrUpdateDto implements Serializable {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {PutGroup.class})
    private Long id;

    /**
     * 类型（对话-chat，向量-embedding，图片-image，语音转文字-transcription，文字转语音-tts）
     */
    @NotBlank(message = "类型不能为空", groups = {PostGroup.class, PutGroup.class})
    @Pattern(regexp = REGEX_MODEL_TYPE_STR, message = "无此模型类型", groups = {GetGroup.class, PutGroup.class})
    private String type;

    /**
     * 服务（deepseek，openai，ollama，zhipu，mistralai，minimax，azureopenai）
     */
    @NotBlank(message = "服务不能为空", groups = {PostGroup.class, PutGroup.class})
    @Pattern(regexp = REGEX_MODEL_SERVICE_STR, message = "无此模型服务", groups = {GetGroup.class, PutGroup.class})
    private String service;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = {PostGroup.class, PutGroup.class})
    private String name;

    /**
     * 服务URL（同AzureOpenAI模型endpoint参数）
     */
    private String baseUrl;

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 响应超时时间
     */
    private Long readTimeout;

    /**
     * 温度（对话模型、语音转文字模型参数）
     */
    private Double temperature;

    /**
     * TopP（对话模型参数）
     */
    private Double topP;

    /**
     * 模型信息（主要用于数据扩展，推荐用JSON格式）
     */
    private String infoName;

    /**
     * 版本（AzureOpenAI模型参数）
     */
    private String version;

    @Serial
    private static final long serialVersionUID = 737303886437092505L;

}
