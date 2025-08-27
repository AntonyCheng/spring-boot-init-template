package top.sharehome.springbootinittemplate.config.ai.spring.vector.model.bo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据块检索Bo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ChunkRetrievalBo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 知识库ID
     */
    private Long knowledgeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 向量数据
     */
    private float[] embedding;

    /**
     * 状态（0表示待训练，1表示训练中，2表示训练成功，3表示训练失败）
     */
    private Integer state;

    /**
     * 向量维度
     */
    private Integer dimension;

    /**
     * 训练失败原因
     */
    private String failReason;

    /**
     * 检索得分（相似度）
     */
    private Double score;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    private Integer deleted;

    @Serial
    private static final long serialVersionUID = -882853647517542229L;

}
