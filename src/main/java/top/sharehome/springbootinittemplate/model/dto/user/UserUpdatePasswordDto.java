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
 * 用户更新密码Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdatePasswordDto implements Serializable {

    private static final long serialVersionUID = -2873349826470166944L;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空", groups = {PutGroup.class})
    private String oldPassword;

    /**
     * 新密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "新密码密码不能为空", groups = {PutGroup.class})
    private String newPassword;

    /**
     * 二次输入的新密码
     */
    @NotBlank(message = "二次新密码不能为空", groups = {PutGroup.class})
    private String checkNewPassword;

}
