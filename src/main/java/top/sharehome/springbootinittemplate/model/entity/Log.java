package top.sharehome.springbootinittemplate.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日志类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_log")
@Accessors(chain = true)
public class Log implements Serializable {

    /**
     * ID
     */
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 接口URI
     */
    @TableField(value = "log_uri")
    private String uri;

    /**
     * 操作描述
     */
    @TableField(value = "log_description")
    private String description;

    /**
     * 操作类型（0其他1增2删3查4改5导入6导出）
     */
    @TableField(value = "log_operator")
    private Integer operator;

    /**
     * 请求方法（RESTFul风格）
     */
    @TableField(value = "log_request_method")
    private String requestMethod;

    /**
     * 方法名称
     */
    @TableField(value = "log_method")
    private String method;

    /**
     * 操作用户ID
     */
    @TableField(value = "log_user_id")
    private Long userId;

    /**
     * 操作用户IP
     */
    @TableField(value = "log_ip")
    private String ip;

    /**
     * 操作用户地点
     */
    @TableField(value = "log_location")
    private String location;

    /**
     * 操作参数
     */
    @TableField(value = "log_param")
    private String param;

    /**
     * 操作结果（0正常1异常）
     */
    @TableField(value = "log_result")
    private Integer result;

    /**
     * 响应内容
     */
    @TableField(value = "log_json")
    private String json;

    /**
     * 接口访问耗时
     */
    @TableField(value = "log_time")
    private Long time;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    @Serial
    private static final long serialVersionUID = -5166630492107189015L;

}