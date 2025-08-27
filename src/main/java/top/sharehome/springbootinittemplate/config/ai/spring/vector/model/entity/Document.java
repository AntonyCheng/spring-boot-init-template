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
 * 文档类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_document")
@Accessors(chain = true)
public class Document implements Serializable {

    /**
     * ID
     */
    @TableId(value = "document_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 知识库ID
     */
    @TableField(value = "document_knowledge_id")
    private Long knowledgeId;

    /**
     * 用户ID
     */
    @TableField(value = "document_user_id")
    private Long userId;

    /**
     * 名称
     */
    @TableField(value = "document_name")
    private String name;

    /**
     * 状态（0表示启用，1表示禁用）
     */
    @TableField(value = "document_state")
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
    private static final long serialVersionUID = -6135466104937525211L;

}
