package top.sharehome.springbootinittemplate.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员修改用户信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserUpdateStateDto implements Serializable {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {PutGroup.class})
    private Long id;

    @Serial
    private static final long serialVersionUID = 6340692579539958471L;
}
