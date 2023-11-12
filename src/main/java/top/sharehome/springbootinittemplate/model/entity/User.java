package top.sharehome.springbootinittemplate.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user")
@Accessors(chain = true)
public class User implements Serializable {

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    @TableField(value = "user_account")
    private String account;

    /**
     * 用户密码
     */
    @TableField(value = "user_password")
    private String password;

    /**
     * 用户昵称
     */
    @TableField(value = "user_name")
    private String name;

    /**
     * 用户头像
     */
    @TableField(value = "user_avatar")
    private String avatar;

    /**
     * 用户角色（admin/user）
     */
    @TableField(value = "user_role", fill = FieldFill.INSERT)
    private String role;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = -5943787044496569074L;

    public static final String COL_USER_ID = "user_id";

    public static final String COL_USER_ACCOUNT = "user_account";

    public static final String COL_USER_PASSWORD = "user_password";

    public static final String COL_USER_NAME = "user_name";

    public static final String COL_USER_AVATAR = "user_avatar";

    public static final String COL_USER_ROLE = "user_role";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";

}