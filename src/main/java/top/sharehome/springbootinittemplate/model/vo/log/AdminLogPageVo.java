package top.sharehome.springbootinittemplate.model.vo.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员分页查询日志信息Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminLogPageVo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 接口URI
     */
    private String uri;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作类型（0其他1增2删3查4改5导入6导出）
     */
    private String operator;

    /**
     * 请求方法（RESTFul风格）
     */
    private String requestMethod;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 操作用户账号
     */
    private String userAccount;

    /**
     * 操作用户IP
     */
    private String ip;

    /**
     * 操作用户地点
     */
    private String location;

    /**
     * 操作参数
     */
    private String param;

    /**
     * 操作结果（0正常1异常）
     */
    private Integer result;

    /**
     * 响应内容
     */
    private String json;

    /**
     * 接口访问耗时
     */
    private Long time;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Serial
    private static final long serialVersionUID = -2469472081812065298L;

}
