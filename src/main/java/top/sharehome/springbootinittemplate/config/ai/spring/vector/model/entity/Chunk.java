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
 * 数据块类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_chunk")
@Accessors(chain = true)
public class Chunk implements Serializable {

    /**
     * ID
     */
    @TableId(value = "chunk_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文档ID
     */
    @TableField(value = "chunk_document_id")
    private Long documentId;

    /**
     * 知识库ID
     */
    @TableField(value = "chunk_knowledge_id")
    private Long knowledgeId;

    /**
     * 用户ID
     */
    @TableField(value = "chunk_user_id")
    private Long userId;

    /**
     * 内容
     */
    @TableField(value = "chunk_content")
    private String content;

    /**
     * 向量数据
     */
    @TableField(value = "chunk_embedding")
    private float[] embedding;

    /**
     * 状态（0表示待训练，1表示训练中，2表示训练成功，3表示训练失败）
     */
    @TableField(value = "chunk_state")
    private Integer state;

    /**
     * 向量维度
     */
    @TableField(value = "chunk_dimension")
    private Integer dimension;

    /**
     * 训练失败原因
     */
    @TableField(value = "chunk_fail_reason")
    private String failReason;

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
    private static final long serialVersionUID = -517412492676051355L;

}
