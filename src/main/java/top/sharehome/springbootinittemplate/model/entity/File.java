package top.sharehome.springbootinittemplate.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_file")
@Accessors(chain = true)
public class File implements Serializable {

    /**
     * ID
     */
    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 唯一摘要值
     */
    @TableField(value = "file_unique_key")
    private String uniqueKey;

    /**
     * 存储名称
     */
    @TableField(value = "file_name")
    private String name;

    /**
     * 原名称
     */
    @TableField(value = "file_original_name")
    private String originalName;

    /**
     * 扩展名
     */
    @TableField(value = "file_suffix")
    private String suffix;

    /**
     * 大小
     */
    @TableField(value = "file_size")
    private Integer size;

    /**
     * 地址
     */
    @TableField(value = "file_url")
    private String url;

    /**
     * OSS类型
     */
    @TableField(value = "file_oss_type")
    private String ossType;

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
    private static final long serialVersionUID = 5521534696303901712L;

}
