package top.sharehome.springbootinittemplate.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户更新名称Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdateNameDto implements Serializable {

    /**
     * 新名称
     */
    @Size(min = 1, max = 16, message = "名称长度介于1-16位之间", groups = {PutGroup.class})
    @NotBlank(message = "名称不能为空", groups = {PutGroup.class})
    private String newName;

    private static final long serialVersionUID = -400810866340194558L;

}
