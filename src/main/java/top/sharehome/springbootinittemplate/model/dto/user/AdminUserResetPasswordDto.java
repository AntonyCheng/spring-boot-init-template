package top.sharehome.springbootinittemplate.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 管理员重置用户密码Dto
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminUserResetPasswordDto implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 新密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "新密码密码不能为空", groups = {PutGroup.class})
    private String newPassword;

    private static final long serialVersionUID = 1519776431197537670L;

}
