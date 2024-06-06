package top.sharehome.springbootinittemplate.model.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员导出用户表格Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserExportVo implements Serializable {

    /**
     * ID
     */
    @ExcelProperty(value = "用户ID")
    private Long id;

    /**
     * 账号
     */
    @ExcelProperty(value = "用户账号")
    private String account;

    /**
     * 名称
     */
    @ExcelProperty(value = "用户名称")
    private String name;

    /**
     * 头像
     */
    @ExcelProperty(value = "用户头像")
    private String avatar;

    /**
     * 角色（admin/user）
     */
    @ExcelProperty(value = "用户角色")
    private String role;

    /**
     * 状态（admin/user）
     */
    @ExcelProperty(value = "用户状态")
    private String state;

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

    private static final long serialVersionUID = -8051043139343116641L;

}
