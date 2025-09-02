package top.sharehome.springbootinittemplate.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员分页查询模型信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelPageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -489242352746869797L;

}
