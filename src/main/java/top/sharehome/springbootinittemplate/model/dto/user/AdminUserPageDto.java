package top.sharehome.springbootinittemplate.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员分页查询用户信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminUserPageDto implements Serializable {

    /**
     * 用户账号（like）
     */
    private String account;

    /**
     * 用户昵称（like）
     */
    private String name;

    /**
     * 用户邮箱（like）
     */
    private String email;

    /**
     * 用户角色（eq）
     */
    private String role;

    /**
     * 用户状态（eq）
     */
    private Integer state;

    @Serial
    private static final long serialVersionUID = -6011180811019851334L;

}
