package top.sharehome.springbootinittemplate.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户MongoDB类（可直接由User类结构粘贴复制过来，有何区别请自行比较）
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@IndexName("t_user")
public class UserMg implements Serializable {

    /**
     * 建议在粘贴复制后将ES中的索引值静态化
     */
    public static final String INDEX = "t_user";

    /**
     * 用户ID
     * 由于在Easy-ES框架有许多方法依赖实体类id，所以这个id值一定是一个必填项
     *
     * @implNote @IndexId注解表示这个是Es实体类的ID，type值为IdType.CUSTOMIZE表示ID需要自定义且不能为null，这里的自定义可以直接查询MySQL数据库中的ID进行插入
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;

    /**
     * 用户账号
     *
     * @implNote @Highlight注解表示需要高亮的字段
     */
    @HighLight(mappingField = "accountHighLight")
    private String account;

    /**
     * 用户账号高亮字段
     */
    private String accountHighLight;

    /**
     * 用户密码
     */
    private String password;

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    private Integer isDeleted;

    private static final long serialVersionUID = -2426597246755636855L;

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