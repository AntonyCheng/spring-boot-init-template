package top.sharehome.springbootinittemplate.model.dto.user;

import jakarta.validation.constraints.*;
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
 * 管理员修改用户信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminUserUpdateInfoDto implements Serializable {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {PutGroup.class})
    private Long id;

    /**
     * 账号
     */
    @Size(min = 2, max = 16, message = "账号长度介于2-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "账号不能为空", groups = {PutGroup.class})
    @Pattern(regexp = REGEX_NUMBER_AND_LETTER, message = "账户名称包含特殊字符", groups = {PutGroup.class})
    private String account;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误", groups = {PostGroup.class})
    @NotBlank(message = "邮箱不能为空", groups = {PostGroup.class})
    private String email;

    /**
     * 名称
     */
    @Size(min = 1, max = 16, message = "名称长度介于1-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "名称不能为空", groups = {PutGroup.class})
    private String name;

    @Serial
    private static final long serialVersionUID = -286594763281759462L;

}
