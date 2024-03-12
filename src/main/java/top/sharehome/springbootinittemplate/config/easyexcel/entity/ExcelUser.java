package top.sharehome.springbootinittemplate.config.easyexcel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.date.ExcelDateConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.date.ExcelLocalDateTimeConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.floatNum.ExcelDoubleConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.floatNum.ExcelFloatConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum.ExcelLongConverter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户Excel类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExcelUser implements Serializable {

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID", converter = ExcelLongConverter.class)
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
    @ExcelProperty(value = "创建时间", converter = ExcelDateConverter.class)
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间", converter = ExcelDateConverter.class)
    private Date updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @ExcelIgnore
    private Integer isDeleted;

    private static final long serialVersionUID = -2270340335058655802L;

}