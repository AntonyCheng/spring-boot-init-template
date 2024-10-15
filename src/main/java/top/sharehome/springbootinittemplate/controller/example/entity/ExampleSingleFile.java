package top.sharehome.springbootinittemplate.controller.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 示例-有关文件上传
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExampleSingleFile implements Serializable {

    /**
     * 文件信息
     */
    @NotNull(message = "文件不能为空", groups = {PostGroup.class})
    private MultipartFile file;

    private static final long serialVersionUID = -1181658901428779461L;

}
