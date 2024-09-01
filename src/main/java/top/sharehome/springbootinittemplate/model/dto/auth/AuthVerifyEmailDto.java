package top.sharehome.springbootinittemplate.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 验证邮箱Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthVerifyEmailDto {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空", groups = {PostGroup.class})
    private String account;

    /**
     * 密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间", groups = {PostGroup.class})
    @NotBlank(message = "密码不能为空", groups = {PostGroup.class})
    private String password;

    /**
     * 二次输入的密码
     */
    @NotBlank(message = "二次密码不能为空", groups = {PostGroup.class})
    private String checkPassword;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不能为空", groups = {PostGroup.class})
    private String emailCode;

    /**
     * 验证码参数实体类
     */
    private Captcha captcha;

}
