package top.sharehome.springbootinittemplate.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthLoginDto implements Serializable {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空", groups = {PostGroup.class})
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空", groups = {PostGroup.class})
    private String password;

    /**
     * 验证码参数实体类
     */
    private Captcha captcha;

    @Serial
    private static final long serialVersionUID = -2121896284587465661L;

}