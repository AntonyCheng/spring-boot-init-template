package top.sharehome.springbootinittemplate.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

import java.io.Serial;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_NUMBER_AND_LETTER_STR;

/**
 * 注册Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthRegisterDto implements Serializable {

    /**
     * 账号
     */
    @Size(min = 2, max = 16, message = "账号长度介于2-16位之间", groups = {PostGroup.class})
    @NotBlank(message = "账号不能为空", groups = {PostGroup.class})
    @Pattern(regexp = REGEX_NUMBER_AND_LETTER_STR, message = "账户名称包含特殊字符", groups = {PostGroup.class})
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
     * 邮箱
     */
    @Email(message = "邮箱格式错误", groups = {PostGroup.class})
    @NotBlank(message = "邮箱不能为空", groups = {PostGroup.class})
    private String email;

    @Serial
    private static final long serialVersionUID = -535414393059407250L;

}