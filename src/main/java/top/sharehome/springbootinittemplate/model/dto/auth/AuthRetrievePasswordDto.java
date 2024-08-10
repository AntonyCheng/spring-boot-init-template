package top.sharehome.springbootinittemplate.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

/**
 * 找回密码Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthRetrievePasswordDto {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空", groups = {PostGroup.class})
    private String account;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误", groups = {PostGroup.class})
    @NotBlank(message = "邮箱不能为空", groups = {PostGroup.class})
    private String email;

    /**
     * 密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间", groups = {PostGroup.class})
    @NotBlank(message = "新密码不能为空", groups = {PostGroup.class})
    private String newPassword;

    /**
     * 二次输入的密码
     */
    @NotBlank(message = "二次新密码不能为空", groups = {PostGroup.class})
    private String checkNewPassword;

    /**
     * 找回密码验证码
     */
    @NotBlank(message = "找回密码验证码不能为空", groups = {PostGroup.class})
    private String passwordCode;

}
