package top.sharehome.springbootinittemplate.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员修改模型信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelUpdateInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -2692918850582813695L;

}
