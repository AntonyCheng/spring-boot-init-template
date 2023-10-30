package top.sharehome.springbootinittemplate.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户登陆Vo类
 *
 * @author AntonyCheng
 * @since 2023/10/4 20:40:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVo implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户角色（admin/user）
     */
    private String role;

    private static final long serialVersionUID = 3559885844441381994L;
}
