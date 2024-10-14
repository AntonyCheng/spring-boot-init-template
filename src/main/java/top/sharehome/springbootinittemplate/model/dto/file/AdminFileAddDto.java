package top.sharehome.springbootinittemplate.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 管理员添加文件Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminFileAddDto implements Serializable {

    /**
     * 文件信息（大小：10MB；格式：任意）
     */
    @NotNull(message = "文件不能为空", groups = {PostGroup.class})
    private MultipartFile file;

    private static final long serialVersionUID = -5683429946233594776L;

}
