package top.sharehome.springbootinittemplate.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_NUMBER_AND_LETTER;

/**
 * 用户更新账号Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdateAccountDto implements Serializable {

    /**
     * 新账号
     */
    @Size(min = 2, max = 16, message = "账号长度介于2-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "账号不能为空", groups = {PutGroup.class})
    @Pattern(regexp = REGEX_NUMBER_AND_LETTER, message = "账户名称包含特殊字符", groups = {PutGroup.class})
    private String newAccount;

    @Serial
    private static final long serialVersionUID = -2873349826470166944L;

}
