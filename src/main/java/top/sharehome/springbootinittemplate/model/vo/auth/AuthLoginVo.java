package top.sharehome.springbootinittemplate.model.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthLoginVo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像ID
     */
    private Long avatarId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色（admin/user）
     */
    private String role;

    @Serial
    private static final long serialVersionUID = 3559885844441381994L;

}