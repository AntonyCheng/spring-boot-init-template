package top.sharehome.springbootinittemplate.model.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员分页查询用户信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LogPageDto implements Serializable {

    /**
     * 接口URI（like）
     */
    private String uri;

    /**
     * 操作描述（like）
     */
    private String description;

    /**
     * 操作类型（0其他1增2删3查4改5导入6导出）（eq）
     */
    private Integer operator;

    /**
     * 请求方法（RESTFul风格）（eq）
     */
    private String requestMethod;

    /**
     * 方法名称（like）
     */
    private String method;

    /**
     * 操作用户账号（like）
     */
    private String userAccount;

    /**
     * 操作用户地点（like）
     */
    private String location;

    /**
     * 操作结果（0正常1异常）（eq）
     */
    private Integer result;

    @Serial
    private static final long serialVersionUID = 4313720201668273852L;

}
