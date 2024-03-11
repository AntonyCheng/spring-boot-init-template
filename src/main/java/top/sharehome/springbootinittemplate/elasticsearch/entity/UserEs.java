package top.sharehome.springbootinittemplate.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户Es类（可直接由User类结构粘贴复制过来，有何区别请自行比较）
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@IndexName("i_user")
public class UserEs implements Serializable {

    /**
     * 建议在粘贴复制后将ES中的索引值静态化
     */
    public static final String INDEX = "i_user";

    /**
     * 主键标识，该属性的值会自动对应ES的主键字段"_id"，这是由ES自动生成索引的唯一ID
     */
    private String id;

    /**
     * 用户ID
     *
     * @implNote IndexField表示索引中的字段名，fieldType指的是该字段在索引中的类型
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long userId;

    /**
     * 用户账号
     *
     * @implNote @Highlight注解表示需要高亮的字段
     */
    @HighLight(mappingField = "accountHighLight")
    @IndexField(value = "account")
    private String account;

    /**
     * 用户账号高亮字段
     */
    private String accountHighLight;

    /**
     * 用户密码
     */
    @IndexField(value = "password")
    private String password;

    /**
     * 用户昵称
     */
    @IndexField(value = "name")
    private String name;

    /**
     * 用户头像
     */
    @IndexField(value = "avatar")
    private String avatar;

    /**
     * 用户角色（admin/user）
     */
    @IndexField(value = "role")
    private String role;

    /**
     * 创建时间
     */
    @IndexField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @IndexField(value = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @IndexField(value = "isDeleted", fieldType = FieldType.INTEGER)
    private Integer isDeleted;

    private static final long serialVersionUID = -2426597246755636855L;

}