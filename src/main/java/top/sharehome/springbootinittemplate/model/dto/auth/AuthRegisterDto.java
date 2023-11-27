package top.sharehome.springbootinittemplate.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_NUMBER_AND_LETTER;

/**
 * 用户注册Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthRegisterDto implements Serializable {

    /**
     * 用户账号
     */
    @Size(min = 2, max = 16, message = "不满足账号长度限制", groups = {PostGroup.class})
    @NotBlank(message = "账号不能为空", groups = {PostGroup.class})
    @Pattern(regexp = REGEX_NUMBER_AND_LETTER, message = "账户名称包含特殊字符", groups = {PostGroup.class})
    private String account;

    /**
     * 用户密码
     */
    @Size(min = 5, max = 16, message = "密码格式不正确", groups = {PostGroup.class})
    @NotBlank(message = "密码不能为空", groups = {PostGroup.class})
    private String password;

    /**
     * 二次输入的密码
     */
    @NotBlank(message = "二次密码不能为空", groups = {PostGroup.class})
    private String checkPassword;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码UUID
     */
    private String uuid;

    private static final long serialVersionUID = -535414393059407250L;

}