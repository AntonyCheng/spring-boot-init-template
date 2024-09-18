package top.sharehome.springbootinittemplate.model.dto.user;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员导出用户模板Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserImportDto implements Serializable {

    /**
     * 账号
     */
    @ExcelProperty(value = "用户账号")
    private String account;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "用户邮箱")
    private String email;

    /**
     * 名称
     */
    @ExcelProperty(value = "用户名称")
    private String name;

    @Serial
    private static final long serialVersionUID = 6306181215565635211L;

}
