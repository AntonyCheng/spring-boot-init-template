package top.sharehome.springbootinittemplate.model.vo.file;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
public class AdminFileExportVo implements Serializable {

    /**
     * ID
     */
    @ExcelProperty(value = "文件ID")
    private Long id;

    /**
     * 唯一摘要值
     */
    @ExcelProperty(value = "唯一摘要值")
    private String uniqueKey;

    /**
     * 存储名称
     */
    @ExcelProperty(value = "存储名称")
    private String name;

    /**
     * 原名称
     */
    @ExcelProperty(value = "原名称")
    private String originalName;

    /**
     * 扩展名
     */
    @ExcelProperty(value = "扩展名")
    private String suffix;

    /**
     * 大小
     */
    @ExcelProperty(value = "大小")
    private String size;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String url;

    /**
     * OSS类型
     */
    @ExcelProperty(value = "OSS类型")
    private String ossType;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 4553170716607916125L;

}
