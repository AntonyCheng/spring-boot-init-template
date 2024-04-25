package top.sharehome.springbootinittemplate.document.excel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户Excel导入导出类
 * 注意：作为Excel导入导出的类，不能编写为链式调用Get/Set，即不允许添加@Accessors(chain = true)注解
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelUser implements Serializable {

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long id;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "用户账号")
    private String account;

    /**
     * 用户密码
     */
    @ExcelProperty(value = "用户密码")
    private String password;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户名称")
    private String name;

    /**
     * 用户头像
     */
    @ExcelProperty(value = "用户头像")
    private String avatar;

    /**
     * 用户角色（admin/user）
     */
    @ExcelProperty(value = "用户角色")
    private String role;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @ExcelIgnore
    private Integer isDeleted;

    private static final long serialVersionUID = -2270340335058655802L;

}