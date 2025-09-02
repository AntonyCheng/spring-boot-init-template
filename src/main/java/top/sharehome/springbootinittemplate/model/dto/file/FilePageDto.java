package top.sharehome.springbootinittemplate.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员分页查询文件信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FilePageDto implements Serializable {

    /**
     * 原名称（like）
     */
    private String originalName;

    /**
     * 扩展名（eq）
     */
    private String suffix;

    /**
     * OSS类型（eq）
     */
    private String ossType;

    @Serial
    private static final long serialVersionUID = 3197129577988764303L;

}
