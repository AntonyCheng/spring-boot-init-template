package top.sharehome.springbootinittemplate.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.GetGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthLoginDto implements Serializable {

    /**
     * 用户账号
     */
    @NotBlank(message = "账号不能为空", groups = {GetGroup.class})
    private String account;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空", groups = {GetGroup.class})
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码UUID
     */
    private String uuid;

    private static final long serialVersionUID = -2121896284587465661L;

}