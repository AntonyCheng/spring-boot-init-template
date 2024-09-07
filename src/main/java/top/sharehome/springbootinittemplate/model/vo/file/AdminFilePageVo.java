package top.sharehome.springbootinittemplate.model.vo.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员分页查询文件信息Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminFilePageVo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 原名称
     */
    private String originalName;

    /**
     * 扩展名
     */
    private String suffix;

    /**
     * 大小
     */
    private String size;

    /**
     * 地址
     */
    private String url;

    /**
     * OSS类型
     */
    private String ossType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Serial
    private static final long serialVersionUID = -4793285381130452793L;

}
