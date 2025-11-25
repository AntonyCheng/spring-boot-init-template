package top.sharehome.springbootinittemplate.model.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模型类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_model")
@Accessors(chain = true)
public class Model implements Serializable {

    /**
     * ID
     */
    @TableId(value = "model_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 类型（对话-chat，向量-embedding，图片-image，语音转文字-transcription，文字转语音-tts）
     */
    @TableField(value = "model_type")
    private String type;

    /**
     * 服务（deepseek，openai，ollama，zhipu，mistralai，minimax，azureopenai）
     */
    @TableField(value = "model_service")
    private String service;

    /**
     * 名称
     */
    @TableField(value = "model_name")
    private String name;

    /**
     * 服务URL（同AzureOpenAI模型endpoint参数）
     */
    @TableField(value = "model_base_url")
    private String baseUrl;

    /**
     * 密钥
     */
    @TableField(value = "model_api_key")
    private String apiKey;

    /**
     * 响应超时时间
     */
    @TableField(value = "model_read_timeout")
    private Long readTimeout;

    /**
     * 模型信息（保存不同类型模型特有的属性信息）
     */
    @TableField(value = "model_info")
    private JSONObject info;

    /**
     * 状态（0表示验证中，1表示启用，2表示不可用，3表示禁用）
     */
    @TableField(value = "model_state", fill = FieldFill.INSERT)
    private Integer state;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;

    @Serial
    private static final long serialVersionUID = -6533833086317886011L;

}