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
     * ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "user_account")
    private String account;

    /**
     * 密码
     */
    @TableField(value = "user_password")
    private String password;

    /**
     * 连续登录失败次数
     */
    @TableField(value = "user_login_num")
    private Integer loginNum;

    /**
     * 名称
     */
    @TableField(value = "user_name")
    private String name;

    /**
     * 头像
     */
    @TableField(value = "user_avatar")
    private String avatar;

    /**
     * 角色（admin/user）
     */
    @TableField(value = "user_role", fill = FieldFill.INSERT)
    private String role;

    /**
     * 状态（0表示启用，1表示禁用）
     */
    @TableField(value = "user_state", fill = FieldFill.INSERT)
    private Integer state;

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

}