package top.sharehome.springbootinittemplate.config.ai.spring.vector.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_knowledge")
@Accessors(chain = true)
public class Knowledge implements Serializable {

    /**
     * ID
     */
    @TableId(value = "knowledge_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "knowledge_user_id")
    private Long userId;

    /**
     * 名称
     */
    @TableField(value = "knowledge_name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "knowledge_description")
    private String description;

    /**
     * 状态（0表示启用，1表示禁用）
     */
    @TableField(value = "knowledge_state")
    private Integer state;

    /**
     * 绑定向量模型ID
     */
    @TableField(value = "knowledge_model_id")
    private Long modelId;

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
    private static final long serialVersionUID = -6910434046136883122L;

}
