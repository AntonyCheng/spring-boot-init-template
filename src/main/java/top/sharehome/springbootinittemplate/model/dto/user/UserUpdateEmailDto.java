package top.sharehome.springbootinittemplate.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新邮箱Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdateEmailDto implements Serializable {

    /**
     * 新名称
     */
    @Email(message = "邮箱格式错误", groups = {PutGroup.class})
    @NotBlank(message = "新邮箱不能为空", groups = {PutGroup.class})
    private String newEmail;

    @Serial
    private static final long serialVersionUID = -5897238396873323519L;

}
