package top.sharehome.springbootinittemplate.model.dto.user;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import java.io.Serial;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_NUMBER_AND_LETTER;

/**
 * 管理员导出用户模板Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserTemplateDto implements Serializable {

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
    private static final long serialVersionUID = -6737409854767719305L;

}
