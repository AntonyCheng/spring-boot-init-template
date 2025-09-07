package top.sharehome.springbootinittemplate.model.vo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员分页查询模型信息Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelPageVo implements Serializable {

    /**
     * 模型ID
     */
    private Long id;

    /**
     * 模型类型（对话-chat，向量-embedding，图片-image，语音转文字-transcription，文字转语音-tts）
     */
    private String type;

    /**
     * 模型服务（deepseek，openai，ollama，zhipu，mistralai，minimax，azureopenai）
     */
    private String service;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型服务URL（同AzureOpenAI模型endpoint参数）
     */
    private String baseUrl;

    /**
     * 模型密钥
     */
    private String apiKey;

    /**
     * 模型响应超时时间
     */
    private Long readTimeout;

    /**
     * 模型温度（对话模型、语音转文字模型参数）
     */
    private Double temperature;

    /**
     * 模型TopP（对话模型参数）
     */
    private Double topP;

    /**
     * 模型结果数量（图像模型参数）
     */
    private Integer n;

    /**
     * 模型信息（主要用于数据扩展，推荐用JSON格式）
     */
    private String info;

    /**
     * 模型版本（AzureOpenAI模型参数）
     */
    private String version;

    /**
     * 模型状态（0表示验证中，1表示启用，2表示不可用，3表示禁用）
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Serial
    private static final long serialVersionUID = -313775093189357282L;

}
