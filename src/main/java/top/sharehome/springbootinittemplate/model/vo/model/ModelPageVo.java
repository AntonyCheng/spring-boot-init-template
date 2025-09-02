package top.sharehome.springbootinittemplate.model.vo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员分页查询模型信息Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelPageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1944282381140862790L;

}
