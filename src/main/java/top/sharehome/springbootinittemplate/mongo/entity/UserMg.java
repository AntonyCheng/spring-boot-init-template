package top.sharehome.springbootinittemplate.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document(collection = "d_user")
public class UserMg implements Serializable {

    /**
     * 建议在粘贴复制后将MongoDB中的文档值静态化
     */
    public static final String DOCUMENT = "d_user";

    /**
     * 主键标识，该属性的值会自动对应mongodb的主键字段"_id"，如果该属性名就叫“id”,则该注解可以省略，否则必须写
     * 如果插入时没有自定义主键，那么该字段必须是String类型，所以建议设计类时就直接将其定为String类型
     */
    @Id
    private String id;

    /**
     * 用户ID
     * 使用@Indexed添加了一个单字段的索引
     */
    @Indexed
    @Field("userId")
    private Long userId;

    /**
     * 用户账号
     * 使用@Field注解使属性对应mongodb的字段的名字，如果一致，则无需该注解
     */
    @Field("account")
    private String account;

    /**
     * 用户密码
     */
    @Field("password")
    private String password;

    /**
     * 用户昵称
     */
    @Field("name")
    private String name;

    /**
     * 用户头像
     */
    @Field("avatar")
    private String avatar;

    /**
     * 用户角色（admin/user）
     */
    @Field("role")
    private String role;

    /**
     * 创建时间
     */
    @Field("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field("updateTime")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @Field("isDeleted")
    private Integer isDeleted;

    private static final long serialVersionUID = -2426597246755636855L;

}