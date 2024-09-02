package top.sharehome.springbootinittemplate.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新头像Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdateAvatarDto implements Serializable {

    /**
     * 用户头像文件（大小：1MB；格式：png或jpg）
     */
    @NotNull(message = "文件不能为空", groups = {PutGroup.class})
    private MultipartFile file;

    @Serial
    private static final long serialVersionUID = 6802990543785064888L;

}
